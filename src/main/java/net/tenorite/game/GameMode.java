package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.tenorite.game.PlayingStats.*;

public abstract class GameMode {

    private static final Comparator<PlayingStats> DEFAULT_COMPARATOR =
        BY_COMBOS.reversed() // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    private final GameModeId id;

    private final Function<Tempo, String> title;

    private final GameRules gameRules;

    private final BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener;

    private final Comparator<PlayingStats> rankComparator;

    protected GameMode(GameModeId id, Function<Tempo, String> title, GameRules gameRules, BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener) {
        this(id, title, gameRules, gameListener, DEFAULT_COMPARATOR);
    }

    protected GameMode(GameModeId id, Function<Tempo, String> title, GameRules gameRules, BiFunction<Scheduler, Consumer<Message>, GameListener> gameListener, Comparator<PlayingStats> rankComparator) {
        this.id = id;
        this.title = title;
        this.gameRules = gameRules;
        this.gameListener = gameListener;
        this.rankComparator = rankComparator;
    }

    public final GameModeId getId() {
        return id;
    }

    public final String getTitle(Tempo tempo) {
        return title.apply(tempo);
    }

    public final GameRules gameRules() {
        return gameRules;
    }

    public final GameListener gameListener(Scheduler scheduler, Consumer<Message> channel) {
        return gameListener.apply(scheduler, channel);
    }

    public final GameRecorder gameRecorder(Tempo tempo, Scheduler scheduler, Consumer<Message> channel) {
        return new GameRecorder(tempo, getId(), gameRules(), gameListener(scheduler, channel));
    }

    public final Comparator<PlayingStats> rankComparator() {
        return rankComparator;
    }

}
