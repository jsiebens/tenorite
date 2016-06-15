package net.tenorite.clients.events;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ClientRegistrationFailed {

    public static ClientRegistrationFailed invalidName() {
        return new ClientRegistrationFailedBuilder().type(Type.INVALID_NAME).build();
    }

    public static ClientRegistrationFailed nameAlreadyInUse() {
        return new ClientRegistrationFailedBuilder().type(Type.NAME_ALREADY_IN_USE).build();
    }

    public static ClientRegistrationFailed of(Type type) {
        return new ClientRegistrationFailedBuilder().type(type).build();
    }

    public enum Type {
        INVALID_NAME,
        NAME_ALREADY_IN_USE
    }

    public abstract Type getType();

}
