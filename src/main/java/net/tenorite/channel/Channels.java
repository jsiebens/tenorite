package net.tenorite.channel;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@ImmutableStyle
public abstract class Channels {

    public static Channels of(Iterable<Channel> channels) {
        return new ChannelsBuilder().channels(channels).build();
    }

    public static Channels of(Channel... channels) {
        return new ChannelsBuilder().addChannels(channels).build();
    }

    public abstract List<Channel> getChannels();

}
