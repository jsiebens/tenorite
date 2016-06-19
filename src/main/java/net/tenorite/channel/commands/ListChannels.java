package net.tenorite.channel.commands;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ListChannels {

    public static ListChannels of(Tempo tempo) {
        return new ListChannelsBuilder().tempo(tempo).build();
    }

    public abstract Tempo getTempo();

}
