package net.tenorite.badges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeBuilder.ImmutableBadge.class)
public abstract class Badge {

    public static Badge of(GameModeId gameModeId, BadgeType badgeType) {
        return new BadgeBuilder().gameModeId(gameModeId).badgeType(badgeType).build();
    }

    public static Badge of(GameModeId gameModeId, String badgeId) {
        return new BadgeBuilder().gameModeId(gameModeId).badgeType(BadgeType.of(badgeId)).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract BadgeType getBadgeType();

}
