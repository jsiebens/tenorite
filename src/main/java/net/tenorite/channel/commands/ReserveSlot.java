package net.tenorite.channel.commands;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ReserveSlot {

    public static ReserveSlot of(String channel, String name) {
        return new ReserveSlotBuilder().channel(channel).name(name).build();
    }

    public abstract String getChannel();

    public abstract String getName();

}
