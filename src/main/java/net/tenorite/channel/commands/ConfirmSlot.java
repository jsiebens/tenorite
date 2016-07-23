package net.tenorite.channel.commands;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class ConfirmSlot {

    public static ConfirmSlot instance() {
        return ConfirmSlotBuilder.ImmutableConfirmSlot.of();
    }

}
