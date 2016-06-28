package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelJoined;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.events.GameFinished;
import net.tenorite.protocol.*;
import net.tenorite.util.AbstractActor;
import net.tenorite.util.CommonsStopWatch;
import net.tenorite.util.Scheduler;
import net.tenorite.util.StopWatch;
import net.tenorite.winlist.events.WinlistUpdated;
import scala.concurrent.duration.FiniteDuration;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static akka.actor.ActorRef.noSender;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class ChannelActor extends AbstractActor {

    static Props props(Tempo tempo, GameMode gameMode, String name) {
        return Props.create(ChannelActor.class, tempo, gameMode, name);
    }

    private static final GameRankCalculator RANK_CALCULATOR = new GameRankCalculator();

    private final Map<ActorRef, Slot> slots = new HashMap<>();

    private final Map<ActorRef, Slot> pending = new HashMap<>();

    private final AvailableSlots availableSlots = new AvailableSlots();

    private final Tempo tempo;

    private final GameMode gameMode;

    private final String name;

    private final Scheduler scheduler;

    private GameRecorder gameRecorder;

    public ChannelActor(Tempo tempo, GameMode gameMode, String name) {
        this.tempo = tempo;
        this.gameMode = gameMode;
        this.name = name;
        this.scheduler = new AkkaScheduler(context().system());
    }

    @Override
    public void preStart() throws Exception {
        subscribe(WinlistUpdated.class);
    }

    @Override
    public void postStop() throws Exception {
        ofNullable(gameRecorder).ifPresent(GameRecorder::stop);
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Message) {
            handleMessage(((Message) o));
        }
        else if (o instanceof ReserveSlot) {
            handleReserveSlot((ReserveSlot) o);
        }
        else if (o instanceof ConfirmSlot) {
            handleConfirmSlot();
        }
        else if (o instanceof LeaveChannel) {
            handleLeaveChannel(sender(), false);
        }
        else if (o instanceof Terminated) {
            handleLeaveChannel(((Terminated) o).actor(), true);
        }
        else if (o instanceof WinlistUpdated) {
            handleWinlistUpdated((WinlistUpdated) o);
        }
    }

    private void handleMessage(Message o) {
        if (o instanceof PlineMessage) {
            handlePline((PlineMessage) o);
        }
        else if (o instanceof PlineActMessage) {
            handlePlineAct((PlineActMessage) o);
        }
        else if (o instanceof GmsgMessage) {
            handleGmsg((GmsgMessage) o);
        }
        else if (o instanceof TeamMessage) {
            handleTeam((TeamMessage) o);
        }
        else if (o instanceof StartGameMessage) {
            handleStartGame((StartGameMessage) o);
        }
        else if (o instanceof StopGameMessage) {
            handleStopGame((StopGameMessage) o);
        }
        else if (o instanceof PauseGameMessage) {
            handlePauseGame((PauseGameMessage) o);
        }
        else if (o instanceof ResumeGameMessage) {
            handleResumeGame((ResumeGameMessage) o);
        }
        else if (o instanceof LvlMessage) {
            handleLvlMessage((LvlMessage) o);
        }
        else if (o instanceof FieldMessage) {
            handleFieldMessage((FieldMessage) o);
        }
        else if (o instanceof SpecialBlockMessage) {
            handleSpecialBlockMessage((SpecialBlockMessage) o);
        }
        else if (o instanceof ClassicStyleAddMessage) {
            handleClassicStyleAddMessage((ClassicStyleAddMessage) o);
        }
        else if (o instanceof PlayerLostMessage) {
            handlePlayerLostMessage((PlayerLostMessage) o);
        }
    }

    private void handleReserveSlot(ReserveSlot o) {
        ActorRef sender = sender();

        if (!pending.containsKey(sender) && !slots.containsKey(sender)) {
            if (slots.size() + pending.size() < 6) {
                int slot = availableSlots.nextSlot();
                pending.put(sender(), new Slot(slot, o.getName(), sender));
                context().watch(sender);
                sender.tell(SlotReserved.instance(), self());
            }
            else {
                sender.tell(SlotReservationFailed.channelIsFull(), self());
            }
        }
    }

    private void handleConfirmSlot() {
        ActorRef sender = sender();
        Slot slot = pending.remove(sender);
        if (slot != null) {

            // send assigned slot number
            slot.send(PlayerNumMessage.of(slot.nr));

            // announce new player
            forEachSlot(p -> p.send(PlayerJoinMessage.of(slot.nr, slot.name)));

            // send current player list
            forEachSlot(p -> {
                slot.send(PlayerJoinMessage.of(p.nr, p.name));
                slot.send(TeamMessage.of(p.nr, ofNullable(p.team).orElse("")));
            });

            // send welcome message
            slot.send(PlineMessage.of(""));
            slot.send(PlineMessage.of(format("Hello <b>%s</b>, welcome in channel <b>%s</b>", slot.name, name)));
            slot.send(PlineMessage.of(""));

            if (gameRecorder != null) {
                slot.send(IngameMessage.of());
                slot.send(gameRecorder.isPaused() ? GamePausedMessage.of() : GameRunningMessage.of());
                forEachSlot(p -> {
                    Field field = gameRecorder.getField(p.nr).orElseGet(Field::randomCompletedField);
                    slot.send(FieldMessage.of(p.nr, field.getFieldString()));
                });
            }

            publish(ChannelJoined.of(tempo, gameMode.getGameModeId(), name, slot.name));

            slots.put(sender(), slot);
        }
    }

    private void handleLeaveChannel(ActorRef actor, boolean disconnected) {
        ofNullable(pending.remove(actor)).ifPresent(p -> availableSlots.releaseSlot(p.nr));

        ofNullable(slots.get(actor)).ifPresent(slot -> {
            if (!disconnected) {
                // clear player list
                forEachSlot(p -> slot.send(PlayerLeaveMessage.of(p.nr)));
            }

            // release slot
            availableSlots.releaseSlot(slot.nr);

            slots.remove(actor);

            // accounce leave in room
            forEachSlot(p -> p.send(PlayerLeaveMessage.of(slot.nr)));

            if (gameRecorder != null) {
                slot.send(EndGameMessage.of());
                gameRecorder.onPlayerLeaveMessage(PlayerLeaveMessage.of(slot.nr)).ifPresent(e -> resetGameRecorder());
            }

            // publish leave
            publish(ChannelLeft.of(tempo, name, slot.name));

            if (!disconnected) {
                actor.tell(ChannelLeft.of(tempo, name, slot.name), self());
                context().unwatch(sender());
            }
        });
    }

    private void handleWinlistUpdated(WinlistUpdated o) {
        if (o.getGameModeId().equals(gameMode.getGameModeId())) {
            forEachSlot(s -> s.send(WinlistMessage.of(o.getItems().stream().map(e -> e.getType().getLetter() + e.getName() + ";" + e.getScore()).collect(toList()))));
        }
    }

    private void handlePline(PlineMessage pline) {
        findSlot(pline.getSender()).ifPresent(s ->
            forEachSlot(p -> p.nr != s.nr, p -> p.send(pline))
        );
    }

    private void handlePlineAct(PlineActMessage plineAct) {
        findSlot(plineAct.getSender()).ifPresent(s ->
            forEachSlot(p -> p.nr != s.nr, p -> p.send(plineAct))
        );
    }

    private void handleGmsg(GmsgMessage gmsg) {
        if (gameRecorder != null) {
            forEachSlot(p -> p.send(gmsg));
        }
    }

    private void handleTeam(TeamMessage o) {
        findSlot(o.getSender()).ifPresent(s ->
            forEachSlot(p -> {
                if (p.nr == o.getSender()) {
                    p.team = o.getTeam();
                }
                else {
                    p.send(o);
                }
            })
        );
    }

    private void handleStartGame(StartGameMessage start) {
        findSlot(start.getSender()).ifPresent(moderator -> {
            if (gameRecorder == null) {
                ActorRef self = self();

                gameRecorder = gameMode.gameRecorder(tempo, scheduler, m -> self.tell(m, noSender()));

                GameRules rules = gameRecorder.start(currentPlayers());

                Message message = PlineMessage.of("<i>game started by <b>" + moderator.name + "</b></i>");
                Message newgame = NewGameMessage.of(rules.toString());

                forEachSlot(s -> {
                    s.send(message);
                    s.send(newgame);
                });
            }
            else {
                moderator.send(PlineMessage.of("<red>game is already running!</red>"));
            }
        });
    }

    private void handleStopGame(StopGameMessage stop) {
        findSlot(stop.getSender()).ifPresent(moderator -> {
            if (gameRecorder != null) {
                gameRecorder.stop();

                Message message = PlineMessage.of("<i>game stopped by <b>" + moderator.name + "</b></i>");
                Message endgame = EndGameMessage.of();

                forEachSlot(s -> {
                    s.send(message);
                    s.send(endgame);
                });

                resetGameRecorder();
            }
            else {
                moderator.send(PlineMessage.of("<red>no running game is available!</red>"));
            }
        });
    }

    private void handlePauseGame(PauseGameMessage pause) {
        findSlot(pause.getSender()).ifPresent(moderator -> {
            if (gameRecorder != null && gameRecorder.pause()) {
                Message message = PlineMessage.of("<i>game paused by <b>" + moderator.name + "</b></i>");
                Message paused = GamePausedMessage.of();

                forEachSlot(p -> {
                    p.send(message);
                    p.send(paused);
                });
            }
            else {
                moderator.send(PlineMessage.of("<red>no running game is available!</red>"));
            }
        });
    }

    private void handleResumeGame(ResumeGameMessage pause) {
        findSlot(pause.getSender()).ifPresent(moderator -> {
            if (gameRecorder != null && gameRecorder.resume()) {
                Message message = PlineMessage.of("<i>game resumed by <b>" + moderator.name + "</b></i>");
                Message running = GameRunningMessage.of();

                forEachSlot(p -> {
                    p.send(message);
                    p.send(running);
                });
            }
            else {
                moderator.send(PlineMessage.of("<red>no paused game is available!</red>"));
            }
        });
    }

    private void handleLvlMessage(LvlMessage lvl) {
        findSlot(lvl.getSender()).ifPresent(player -> {
            if (gameRecorder != null) {
                gameRecorder.onLvlMessage(lvl);
                forEachSlot(op -> op.send(lvl));
            }
        });
    }

    private void handleFieldMessage(FieldMessage field) {
        findSlot(field.getSender()).ifPresent(player -> {
            if (gameRecorder != null) {
                gameRecorder.onFieldMessage(field);
                forEachSlot(op -> op.nr != player.nr, op -> op.send(field));
            }
        });
    }

    private void handleSpecialBlockMessage(SpecialBlockMessage message) {
        findSlot(message.getSender()).ifPresent(player -> {
            if (gameRecorder != null) {
                gameRecorder.onSpecialBlockMessage(message);
                forEachSlot(op -> op.nr != player.nr, op -> op.send(message));
            }
        });
    }

    private void handleClassicStyleAddMessage(ClassicStyleAddMessage message) {
        if (message.getSender() == 0) {
            if (gameRecorder != null && gameRecorder.onClassicStyleAddMessage(message)) {
                forEachSlot(op -> op.send(message));
            }
        }
        else {
            findSlot(message.getSender()).ifPresent(player -> {
                if (gameRecorder != null && gameRecorder.onClassicStyleAddMessage(message)) {
                    forEachSlot(op -> op.nr != player.nr, op -> op.send(message));
                }
            });
        }
    }

    private void handlePlayerLostMessage(PlayerLostMessage message) {
        findSlot(message.getSender()).ifPresent(loser -> {
            if (gameRecorder != null) {
                forEachSlot(s -> s.send(message));
                gameRecorder.onPlayerLostMessage(message).ifPresent(this::endGame);
            }
        });
    }

    private void endGame(Game game) {
        forEachSlot(p -> p.send(EndGameMessage.of()));

        List<PlayingStats> ranking = RANK_CALCULATOR.calculate(gameMode, game);

        if (!ranking.isEmpty()) {
            displayStats(game, ranking);
            publish(GameFinished.of(game, ranking));
        }

        if (ranking.size() > 1) {
            forEachSlot(p -> p.send(PlayerWonMessage.of(ranking.get(0).getPlayer().getSlot())));
        }

        resetGameRecorder();
    }

    private void resetGameRecorder() {
        this.gameRecorder = null;
    }

    private final DecimalFormat df = new DecimalFormat("0.00");

    private void displayStats(Game game, List<PlayingStats> ranking) {
        ranking.forEach(ps -> {
            String bpm = df.format((double) ps.getNrOfBlocks() * 60000 / (double) ps.getPlayingTime());
            String part1 = format("<purple><b>%s</b></purple>: <aqua>%d blocks @ %s bpm</aqua>", ps.getPlayer().getName(), ps.getNrOfBlocks(), bpm);
            String part2 = format("<blue>%s/%s/%s specials</blue>", ps.getTotalNrOfSpecialsOnOpponent() + ps.getTotalNrOfSpecialsOnTeamPlayer(), ps.getTotalNrOfSpecialsReceived(), ps.getTotalNrOfSpecialsOnSelf());
            String part3 = format("%s/%s/%s combos", ps.getNrOfTwoLineCombos(), ps.getNrOfThreeLineCombos(), ps.getNrOfFourLineCombos());
            String part4 = format("lvl: %s, mxfh: %s", ps.getLevel(), ps.getMaxFieldHeight());

            forEachSlot(s -> s.send(PlineMessage.of(part1 + "; " + part2 + "; " + part3 + "; " + part4)));
        });

        forEachSlot(s -> s.send(PlineMessage.of("<brown>Total game time: <black>" + df.format(game.getDuration() / 1000f) + "</black> seconds")));
    }

    // =================================================================================================================

    private void forEachSlot(Consumer<Slot> playerConsumer) {
        slots.values().stream().forEach(playerConsumer);
    }

    private void forEachSlot(Predicate<Slot> predicate, Consumer<Slot> playerConsumer) {
        slots.values().stream().filter(predicate).forEach(playerConsumer);
    }

    private Optional<Slot> findSlot(int slot) {
        return slots.values().stream().filter(p -> p.nr == slot).findFirst();
    }

    private List<Player> currentPlayers() {
        return slots.values().stream().map(Slot::player).collect(toList());
    }

    private static final class AvailableSlots {

        private final LinkedList<Integer> slots;

        AvailableSlots() {
            this.slots = new LinkedList<>(range(1, 7).boxed().collect(toList()));
        }

        int nextSlot() {
            return slots.removeFirst();
        }

        void releaseSlot(int slot) {
            slots.add(slot);
            Collections.sort(slots);
        }

    }

    private static final class Slot {

        private final int nr;

        private final String name;

        private final ActorRef actor;

        private String team;

        Slot(int nr, String name, ActorRef actor) {
            this.nr = nr;
            this.name = name;
            this.actor = actor;
        }

        void send(Message m) {
            actor.tell(m, noSender());
        }

        Player player() {
            return Player.of(nr, name, team);
        }

    }

    private static class AkkaScheduler implements Scheduler {

        private final ActorSystem sytem;

        public AkkaScheduler(ActorSystem sytem) {
            this.sytem = sytem;
        }

        @Override
        public Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable task) {
            FiniteDuration duration = FiniteDuration.create(delay, timeUnit);
            akka.actor.Cancellable c = sytem.scheduler().scheduleOnce(duration, task, sytem.dispatcher());
            return c::cancel;
        }

        @Override
        public Cancellable schedule(long initial, long delay, TimeUnit timeUnit, Runnable task) {
            FiniteDuration initialDuration = FiniteDuration.create(initial, timeUnit);
            FiniteDuration delayDuration = FiniteDuration.create(delay, timeUnit);
            akka.actor.Cancellable c = sytem.scheduler().schedule(initialDuration, delayDuration, task, sytem.dispatcher());
            return c::cancel;
        }

        @Override
        public StopWatch stopWatch() {
            return new CommonsStopWatch();
        }

    }

}
