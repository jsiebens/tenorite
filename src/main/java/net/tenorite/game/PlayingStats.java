package net.tenorite.game;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class PlayingStats {

    public static PlayingStats of(Player player) {
        return new PlayingStatsBuilder().player(player).build();
    }

    public static PlayingStats of(Player player, long playingTime, int level, int nrOfLines) {
        return new PlayingStatsBuilder().player(player).playingTime(playingTime).level(level).nrOfLines(nrOfLines).build();
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

}
