package net.tenorite.channel.events;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class ChannelLeft {

    public static ChannelLeft of(Tempo tempo, String channel, String name) {
        return new ChannelLeftBuilder().tempo(tempo).channel(channel).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract String getChannel();

    public abstract String getName();

}
