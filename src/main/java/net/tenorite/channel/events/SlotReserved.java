package net.tenorite.channel.events;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class SlotReserved {

    public static SlotReserved instance() {
        return SlotReservedBuilder.ImmutableSlotReserved.of();
    }

}
