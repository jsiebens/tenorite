package net.tenorite.channel;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class Channel {

    public static Channel of(String name) {
        return new ChannelBuilder().name(name).build();
    }

    public abstract String getName();

}
