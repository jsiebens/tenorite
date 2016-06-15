package net.tenorite.protocol;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class Inbound {

    public static Inbound of(String message) {
        return new InboundBuilder().message(message).build();
    }

    public abstract String getMessage();

}
