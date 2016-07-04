package net.tenorite.badges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeLevelBuilder.ImmutableBadgeLevel.class)
public abstract class BadgeLevel {

    public static BadgeLevel of(String name, Badge badge, long timestamp, long level, String gameId) {
        return new BadgeLevelBuilder().name(name).gameModeId(badge.getGameModeId()).badgeType(badge.getBadgeType()).timestamp(timestamp).level(level).gameId(gameId).build();
    }

    public abstract String getName();

    public abstract GameModeId getGameModeId();

    public abstract BadgeType getBadgeType();

    public abstract long getTimestamp();

    public abstract long getLevel();

    public abstract String getGameId();

}
