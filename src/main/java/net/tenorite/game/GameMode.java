package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class GameMode {

    private final GameModeId gameModeId;

    private final GameRules gameRules;

    private final BiFunction<Scheduler, Consumer<Message>, GameListener> gameRecorder;

    protected GameMode(GameModeId gameModeId, GameRules gameRules, BiFunction<Scheduler, Consumer<Message>, GameListener> gameRecorder) {
        this.gameModeId = gameModeId;
        this.gameRules = gameRules;
        this.gameRecorder = gameRecorder;
    }

    public final GameModeId getGameModeId() {
        return gameModeId;
    }

    public final GameRules gameRules() {
        return gameRules;
    }

    public final GameListener gameListener(Scheduler scheduler, Consumer<Message> channel) {
        return gameRecorder.apply(scheduler, channel);
    }

    public final GameRecorder gameRecorder(Tempo tempo, Scheduler scheduler, Consumer<Message> channel) {
        return new GameRecorder(tempo, getGameModeId(), gameRules(), gameListener(scheduler, channel));
    }

}
