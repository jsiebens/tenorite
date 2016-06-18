package net.tenorite.channel.commands;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ReserveSlot {

    public static ReserveSlot of(Tempo tempo, String channel, String name) {
        return new ReserveSlotBuilder().tempo(tempo).channel(channel).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract String getChannel();

    public abstract String getName();

}
