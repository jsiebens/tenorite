package net.tenorite.game;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

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
                                  int maxFieldHeight
    ) {
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

}
