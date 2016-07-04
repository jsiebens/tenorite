package net.tenorite.badges.events;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class BadgeEarned {

    public static BadgeEarned of(BadgeLevel badge, boolean upgrade) {
        return new BadgeEarnedBuilder().badge(badge).upgrade(upgrade).build();
    }

    public abstract BadgeLevel getBadge();

    public abstract boolean isUpgrade();

}
