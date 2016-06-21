package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerLeaveMessage;
import net.tenorite.protocol.PlayerLostMessage;
import net.tenorite.util.Scheduler;
import net.tenorite.util.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
        return gameMode.getGameRules();
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

    public boolean onFieldMessage(FieldMessage fieldMessage) {
        int slot = fieldMessage.getSender();
        return Optional.ofNullable(fields.computeIfPresent(slot, (s, f) -> f.update(fieldMessage.getUpdate()))).isPresent();
    }

    public Optional<Game> onPlayerLeaveMessage(PlayerLeaveMessage playerLeaveMessage) {
        messages.add(GameMessage.of(stopWatch.getTime(), playerLeaveMessage));

        if (slots.remove(playerLeaveMessage.getSender()) != null && slots.isEmpty()) {
            return of(finishRecording());
        }
        else {
            return empty();
        }
    }

    public Optional<Game> onPlayerLostMessage(PlayerLostMessage playerLostMessage) {
        messages.add(GameMessage.of(stopWatch.getTime(), playerLostMessage));
        Player loser = slots.remove(playerLostMessage.getSender());
        return loser != null && (slots.isEmpty() || teamCount() <= 1) ? of(finishRecording()) : empty();
    }

    public Optional<Field> getField(int slot) {
        return ofNullable(fields.get(slot));
    }

    public boolean isPaused() {
        return stopWatch.isSuspended();
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

}
