package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class GmsgMessage implements Message {

    public static GmsgMessage of(String message) {
        return new GmsgMessageBuilder().message(message).build();
    }

    public abstract String getMessage();

    @Override
    public String raw(Tempo tempo) {
        return String.format("gmsg %s", getMessage());
    }

}
