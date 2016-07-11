package net.tenorite.badges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeLevelBuilder.ImmutableBadgeLevel.class)
public abstract class BadgeLevel {

    public static BadgeLevel of(Tempo tempo, Badge badge, String name, long timestamp, long level, String gameId) {
        return
            new BadgeLevelBuilder()
                .tempo(tempo)
                .gameModeId(badge.getGameModeId())
                .badgeType(badge.getBadgeType())
                .name(name)
                .timestamp(timestamp)
                .level(level)
                .gameId(gameId)
                .build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract BadgeType getBadgeType();

    public abstract String getName();

    public abstract long getTimestamp();

    public abstract long getLevel();

    public abstract String getGameId();

}
