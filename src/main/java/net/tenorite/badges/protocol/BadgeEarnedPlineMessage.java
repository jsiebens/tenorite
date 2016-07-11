package net.tenorite.badges.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import static java.lang.String.format;

@ImmutableStyle
@Value.Immutable
public abstract class BadgeEarnedPlineMessage implements Message {

    public static BadgeEarnedPlineMessage of(String name, String title, long level, boolean upgrade) {
        return new BadgeEarnedPlineMessageBuilder().name(name).title(title).level(level).upgrade(upgrade).build();
    }

    public abstract String getName();

    public abstract String getTitle();

    public abstract long getLevel();

    public abstract boolean isUpgrade();

    @Override
    public String raw(Tempo tempo) {
        return isUpgrade() ?
            format("pline 0 <brown><b>%s</b> upgraded a badge <b>%s</b> - Level %s</brown>", getName(), getTitle(), getLevel()) :
            format("pline 0 <brown><b>%s</b> earned a badge <b>%s</b> - Level %s</brown>", getName(), getTitle(), getLevel());
    }

}
