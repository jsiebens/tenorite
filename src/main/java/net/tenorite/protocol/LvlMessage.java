package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class LvlMessage implements Message {

    public static LvlMessage of(int sender, int level) {
        return new LvlMessageBuilder().sender(sender).level(level).build();
    }

    public abstract int getSender();

    public abstract int getLevel();

    @Override
    public String raw(Tempo tempo) {
        return String.format("lvl %s %s", getSender(), getLevel());
    }

}
