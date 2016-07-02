package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.Comparator;
import java.util.function.Consumer;

import static net.tenorite.game.PlayingStats.*;

public abstract class GameMode {

    private static final Comparator<PlayingStats> DEFAULT_COMPARATOR =
        BY_COMBOS.reversed() // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    private final GameModeId id;

    private final GameRules gameRules;

    protected GameMode(GameModeId id, GameRules gameRules) {
        this.id = id;
        this.gameRules = gameRules;
    }

    public final GameModeId getId() {
        return id;
    }

    public final GameRules getGameRules() {
        return gameRules;
    }

    public String getTitle(Tempo tempo) {
        return id.toString();
    }

    public String getDescription(Tempo tempo) {
        return "";
    }

    public Comparator<PlayingStats> getPlayingStatsComparator() {
        return DEFAULT_COMPARATOR;
    }

    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new SuddenDeath(300, 10, 1, scheduler, channel);
    }

}
