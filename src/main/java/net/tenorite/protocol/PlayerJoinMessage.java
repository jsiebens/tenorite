package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class PlayerJoinMessage implements Message {

    public static PlayerJoinMessage of(int sender, String name) {
        return new PlayerJoinMessageBuilder().sender(sender).name(name).build();
    }

    public abstract int getSender();

    public abstract String getName();

    @Override
    public String raw(Tempo tempo) {
        return String.format("playerjoin %s %s", getSender(), getName());
    }

}
