package net.tenorite.game;

import net.tenorite.core.Special;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class PlayingStats {

    public static final Comparator<PlayingStats> BY_LEVEL = (o1, o2) -> o1.getLevel() - o2.getLevel();

    public static final Comparator<PlayingStats> BY_BLOCKS = (o1, o2) -> o1.getNrOfBlocks() - o2.getNrOfBlocks();

    public static final Comparator<PlayingStats> BY_COMBOS = (o1, o2) -> o1.getNrOfCombos() - o2.getNrOfCombos();

    public static final Comparator<PlayingStats> BY_MAX_HEIGTH = (o1, o2) -> o1.getMaxFieldHeight() - o2.getMaxFieldHeight();

    public static PlayingStats of(Player player) {
        return new PlayingStatsBuilder().player(player).build();
    }

    public static PlayingStats of(Player player, Consumer<PlayingStatsBuilder> consumer) {
        PlayingStatsBuilder builder = new PlayingStatsBuilder().player(player);
        consumer.accept(builder);
        return builder.build();
    }

    public static PlayingStats of(Player player, long playingTime, int level, int nrOfLines,
                                  int nrOfTwoLineCombos,
                                  int nrOfThreeLineCombos,
                                  int nrOfFourLineCombos,
                                  int lastFieldHeight,
                                  int maxFieldHeight,
                                  int nrOfBlocks,
                                  String specialsSequence,
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
                .specialsSequence(specialsSequence)
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

    @Value.Default
    public String getSpecialsSequence() {
        return "";
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

    public final int getTotalNrOfSpecialsUsed(Predicate<Special> predicate) {
        int onOpponent = getNrOfSpecialsOnOpponent().entrySet().stream().filter(e -> predicate.test(e.getKey())).mapToInt(Map.Entry::getValue).sum();
        int onTeamPlayer = getNrOfSpecialsOnTeamPlayer().entrySet().stream().filter(e -> predicate.test(e.getKey())).mapToInt(Map.Entry::getValue).sum();
        int onSelf = getNrOfSpecialsOnSelf().entrySet().stream().filter(e -> predicate.test(e.getKey())).mapToInt(Map.Entry::getValue).sum();

        return onOpponent + onTeamPlayer + onSelf;
    }
    
}
