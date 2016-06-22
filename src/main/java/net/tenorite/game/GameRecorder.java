package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import net.tenorite.util.Scheduler;
import net.tenorite.util.StopWatch;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Optional.*;
import static java.util.stream.Collectors.toMap;

public final class GameRecorder {

    private final String id;

    private final Tempo tempo;

    private final GameMode gameMode;

    private final List<Player> players;

    private final StopWatch stopWatch;

    private final SuddenDeathMonitor suddenDeathMonitor;

    private final Map<Integer, Field> fields = new HashMap<>();

    private final Map<Integer, Player> slots = new HashMap<>();

    private final List<GameMessage> messages = new ArrayList<>();

    public GameRecorder(Tempo tempo, GameMode gameMode, List<Player> players, Scheduler scheduler, Consumer<Message> channel) {
        this.id = newGameId();
        this.tempo = tempo;
        this.gameMode = gameMode;

        this.players = Collections.unmodifiableList(players);
        this.fields.putAll(players.stream().collect(toMap(Player::getSlot, p -> Field.empty())));
        this.slots.putAll(players.stream().collect(toMap(Player::getSlot, Function.identity())));

        this.stopWatch = scheduler.stopWatch();
        this.suddenDeathMonitor = new SuddenDeathMonitor(gameMode.getSuddenDeath(), scheduler, channel);
    }

    public GameRules start() {
        stopWatch.start();
        suddenDeathMonitor.start();
        return GameRules.from(gameMode.getGameRules(), b -> b.classicRules(true));
    }

    public void stop() {
        if (stopWatch.isStarted() || stopWatch.isSuspended()) {
            finishRecording();
        }
    }

    public boolean pause() {
        if (stopWatch.isStarted()) {
            stopWatch.suspend();
            suddenDeathMonitor.pause();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean resume() {
        if (stopWatch.isSuspended()) {
            stopWatch.resume();
            suddenDeathMonitor.resume();
            return true;
        }
        else {
            return false;
        }
    }

    public void onLvlMessage(LvlMessage lvlMessage) {
        recordMessage(stopWatch.getTime(), lvlMessage);
    }

    public void onSpecialBlockMessage(SpecialBlockMessage specialBlockMessage) {
        recordMessage(stopWatch.getTime(), specialBlockMessage);
    }

    public boolean onClassicStyleAddMessage(ClassicStyleAddMessage classicStyleAddMessage) {
        recordMessage(stopWatch.getTime(), classicStyleAddMessage);
        return gameMode.getGameRules().getClassicRules() || classicStyleAddMessage.getSender() == 0;
    }

    public void onFieldMessage(FieldMessage fieldMessage) {
        int slot = fieldMessage.getSender();
        ofNullable(fields.computeIfPresent(slot, updateWith(fieldMessage.getUpdate())))
            .ifPresent(f -> recordMessage(stopWatch.getTime(), FieldMessage.of(slot, f.getFieldString())));
    }

    public Optional<Game> onPlayerLeaveMessage(PlayerLeaveMessage playerLeaveMessage) {
        recordMessage(stopWatch.getTime(), playerLeaveMessage);

        if (slots.remove(playerLeaveMessage.getSender()) != null && slots.isEmpty()) {
            return of(finishRecording());
        }
        else {
            return empty();
        }
    }

    public Optional<Game> onPlayerLostMessage(PlayerLostMessage playerLostMessage) {
        recordMessage(stopWatch.getTime(), playerLostMessage);
        Player loser = slots.remove(playerLostMessage.getSender());
        return loser != null && (slots.isEmpty() || teamCount() <= 1) ? of(finishRecording()) : empty();
    }

    public Optional<Field> getField(int slot) {
        return ofNullable(fields.get(slot));
    }

    public boolean isPaused() {
        return stopWatch.isSuspended();
    }

    private void recordMessage(long time, Message message) {
        messages.add(GameMessage.of(time, message));
    }

    private Game finishRecording() {
        stopWatch.stop();
        suddenDeathMonitor.stop();
        return Game.of(id, stopWatch.getStartTime(), stopWatch.getTime(), tempo, gameMode, players, messages);
    }

    private long teamCount() {
        long a = slots.values().stream().filter(Player::isTeamPlayer).map(Player::getTeam).distinct().count();
        long b = slots.values().stream().filter(Player::isSoloPlayer).map(Player::getName).count();
        return a + b;
    }

    private static String newGameId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private static BiFunction<Integer, Field, Field> updateWith(String update) {
        return (k, f) -> f.update(update);
    }

}
