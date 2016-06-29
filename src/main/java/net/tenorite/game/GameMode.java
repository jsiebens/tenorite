package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static net.tenorite.game.PlayingStats.*;

public abstract class GameMode {

    private static final Comparator<PlayingStats> DEFAULT_COMPARATOR =
        BY_COMBOS.reversed() // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    private final GameModeId gameModeId;

    private final GameRules gameRules;

    private final BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener;

    private final Comparator<PlayingStats> rankComparator;

    protected GameMode(GameModeId gameModeId, GameRules gameRules, BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener) {
        this(gameModeId, gameRules, gameListener, DEFAULT_COMPARATOR);
    }

    protected GameMode(GameModeId gameModeId, GameRules gameRules, BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener, Comparator<PlayingStats> rankComparator) {
        this.gameModeId = gameModeId;
        this.gameRules = gameRules;
        this.gameListener = gameListener;
        this.rankComparator = rankComparator;
    }

    public final GameModeId getGameModeId() {
        return gameModeId;
    }

    public final GameRules gameRules() {
        return gameRules;
    }

    public final GameListener gameListener(Scheduler scheduler, Consumer<Message> channel) {
        return gameListener.apply(scheduler, channel);
    }

    public final GameRecorder gameRecorder(Tempo tempo, Scheduler scheduler, Consumer<Message> channel) {
        return new GameRecorder(tempo, getGameModeId(), gameRules(), gameListener(scheduler, channel));
    }

    public final Comparator<PlayingStats> rankComparator() {
        return rankComparator;
    }

}
