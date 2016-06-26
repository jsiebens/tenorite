package net.tenorite.game;

import net.tenorite.core.Special;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@ImmutableStyle
public abstract class PlayingStats {

    public static PlayingStats of(Player player) {
        return new PlayingStatsBuilder().player(player).build();
    }

    public static PlayingStats of(Player player, long playingTime, int level, int nrOfLines,
                                  int nrOfTwoLineCombos,
                                  int nrOfThreeLineCombos,
                                  int nrOfFourLineCombos,
                                  int lastFieldHeight,
                                  int maxFieldHeight,
                                  int nrOfBlocks,
                                  Map<Special, Integer> nrOfSpecialsReceived,
                                  Map<Special, Integer> nrOfSpecialsOnOpponent,
                                  Map<Special, Integer> nrOfSpecialsOnTeamPlayer,
                                  Map<Special, Integer> nrOfSpecialsOnSelf) {
        return
            new PlayingStatsBuilder()
                .player(player)
                .playingTime(playingTime)
                .level(level)
                .nrOfLines(nrOfLines)
                .nrOfTwoLineCombos(nrOfTwoLineCombos)
                .nrOfThreeLineCombos(nrOfThreeLineCombos)
                .nrOfFourLineCombos(nrOfFourLineCombos)
                .lastFieldHeight(lastFieldHeight)
                .maxFieldHeight(maxFieldHeight)
                .nrOfBlocks(nrOfBlocks)
                .nrOfSpecialsReceived(nrOfSpecialsReceived)
                .nrOfSpecialsOnOpponent(nrOfSpecialsOnOpponent)
                .nrOfSpecialsOnTeamPlayer(nrOfSpecialsOnTeamPlayer)
                .nrOfSpecialsOnSelf(nrOfSpecialsOnSelf)
                .build();
    }

    public abstract Player getPlayer();

    @Value.Default
    public long getPlayingTime() {
        return 0;
    }

    @Value.Default
    public int getLevel() {
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

    @Value.Lazy
    public int getNrOfCombos() {
        return getNrOfTwoLineCombos() + getNrOfThreeLineCombos() + getNrOfFourLineCombos();
    }

    @Value.Default
    public int getMaxFieldHeight() {
        return 0;
    }

    @Value.Default
    public int getLastFieldHeight() {
        return 0;
    }

    @Value.Default
    public int getNrOfBlocks() {
        return 0;
    }

    public abstract Map<Special, Integer> getNrOfSpecialsReceived();

    public abstract Map<Special, Integer> getNrOfSpecialsOnOpponent();

    public abstract Map<Special, Integer> getNrOfSpecialsOnTeamPlayer();

    public abstract Map<Special, Integer> getNrOfSpecialsOnSelf();

}
