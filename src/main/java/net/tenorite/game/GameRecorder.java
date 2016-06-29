package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import net.tenorite.util.CommonsStopWatch;
import net.tenorite.util.StopWatch;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Optional.*;
import static java.util.stream.Collectors.toMap;

public final class GameRecorder {

    private final String id;

    private final Tempo tempo;

    private final GameModeId gameModeId;

    private final StopWatch stopWatch;

    private final GameListener listener;

    private final GameRules gameRules;

    private List<Player> players;

    private Map<Integer, Field> fields = new HashMap<>();

    private Map<Integer, Player> slots = new HashMap<>();

    private List<GameMessage> messages = new ArrayList<>();

    public GameRecorder(Tempo tempo, GameModeId gameModeId, GameRules gameRules, GameListener gameListener) {
        this.id = newGameId();
        this.tempo = tempo;
        this.gameModeId = gameModeId;

        this.stopWatch = new CommonsStopWatch();
        this.gameRules = gameRules;
        this.listener = gameListener;
    }

    public GameRules start(List<Player> players) {
        this.players = Collections.unmodifiableList(players);
        this.fields.putAll(players.stream().collect(toMap(Player::getSlot, p -> Field.empty())));
        this.slots.putAll(players.stream().collect(toMap(Player::getSlot, Function.identity())));

        stopWatch.start();
        listener.onStartGame(players);
        return GameRules.from(gameRules, b -> b.classicRules(true));
    }

    public void stop() {
        if (stopWatch.isStarted() || stopWatch.isSuspended()) {
            finishRecording();
        }
    }

    public boolean pause() {
        if (stopWatch.isStarted()) {
            stopWatch.suspend();
            listener.onPauseGame();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean resume() {
        if (stopWatch.isSuspended()) {
            stopWatch.resume();
            listener.onResumeGame();
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
        Player sender = slots.get(specialBlockMessage.getSender());
        Player target = slots.get(specialBlockMessage.getTarget());

        if (sender != null && target != null) {
            listener.onSpecial(sender, specialBlockMessage.getSpecial(), target);
            recordMessage(stopWatch.getTime(), specialBlockMessage);
        }
    }

    public boolean onClassicStyleAddMessage(ClassicStyleAddMessage classicStyleAddMessage) {
        if (classicStyleAddMessage.getSender() == 0) {
            recordMessage(stopWatch.getTime(), classicStyleAddMessage);
            return true;
        }
        else {
            Player sender = slots.get(classicStyleAddMessage.getSender());
            if (sender != null) {
                listener.onClassicStyleAdd(sender, classicStyleAddMessage.getLines());
                recordMessage(stopWatch.getTime(), classicStyleAddMessage);
                return gameRules.getClassicRules();
            }
        }
        return false;
    }

    public void onFieldMessage(FieldMessage fieldMessage) {
        Player sender = slots.get(fieldMessage.getSender());
        if (sender != null) {
            Field field = fields.computeIfPresent(sender.getSlot(), updateWith(fieldMessage.getUpdate()));
            listener.onFieldUpdate(sender, field);
            recordMessage(stopWatch.getTime(), FieldMessage.of(sender.getSlot(), field.getFieldString()));
        }
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

    public Game onPlayerWonMessage(PlayerWonMessage playerWonMessage) {
        messages.add(GameMessage.of(stopWatch.getTime(), playerWonMessage));
        return finishRecording();
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
        listener.onEndGame();
        return Game.of(id, stopWatch.getStartTime(), stopWatch.getTime(), tempo, gameModeId, players, messages);
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
