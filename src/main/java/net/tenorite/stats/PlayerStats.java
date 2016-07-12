package net.tenorite.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.core.Special;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = PlayerStatsBuilder.ImmutablePlayerStats.class)
public abstract class PlayerStats {

    public static PlayerStats of(GameModeId gameModeId, String name) {
        return new PlayerStatsBuilder().gameModeId(gameModeId).name(name).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getName();

    @Value.Default
    public long getGamesPlayed() {
        return 0;
    }

    @Value.Default
    public long getGamesWon() {
        return 0;
    }

    @Value.Default
    public long getTimePlayed() {
        return 0;
    }

    @Value.Default
    public int getNrOfBlocks() {
        return 0;
    }

    @Value.Default
    public int getNrOfLines() {
        return 0;
    }

    @Value.Default
    public int getNrOfTwoLineCombos() {
        return 0;
    }

    @Value.Default
    public int getNrOfThreeLineCombos() {
        return 0;
    }

    @Value.Default
    public int getNrOfFourLineCombos() {
        return 0;
    }

    public abstract Map<Special, Integer> getNrOfSpecialsReceived();

    public abstract Map<Special, Integer> getNrOfSpecialsOnOpponent();

    public abstract Map<Special, Integer> getNrOfSpecialsOnTeamPlayer();

    public abstract Map<Special, Integer> getNrOfSpecialsOnSelf();

    @Value.Lazy
    public int getTotalNrOfSpecialsReceived() {
        return getNrOfSpecialsReceived().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnOpponent() {
        return getNrOfSpecialsOnOpponent().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnTeamPlayer() {
        return getNrOfSpecialsOnTeamPlayer().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnSelf() {
        return getNrOfSpecialsOnSelf().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsUsed() {
        return getTotalNrOfSpecialsOnOpponent() + getTotalNrOfSpecialsOnTeamPlayer() + getTotalNrOfSpecialsOnSelf();
    }

}
