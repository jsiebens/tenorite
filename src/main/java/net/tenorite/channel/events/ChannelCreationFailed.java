package net.tenorite.channel.events;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ChannelCreationFailed {

    public static ChannelCreationFailed invalidName() {
        return new ChannelCreationFailedBuilder().type(Type.INVALID_NAME).build();
    }

    public static ChannelCreationFailed nameAlreadyInUse() {
        return new ChannelCreationFailedBuilder().type(Type.NAME_ALREADY_IN_USE).build();
    }

    private static ChannelCreationFailed of(Type type) {
        return new ChannelCreationFailedBuilder().type(type).build();
    }

    public enum Type {
        INVALID_NAME,
        NAME_ALREADY_IN_USE
    }

    public abstract Type getType();

}
