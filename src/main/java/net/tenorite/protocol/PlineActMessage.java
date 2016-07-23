package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class PlineActMessage implements Message {

    public static PlineActMessage of(int sender, String message) {
        return new PlineActMessageBuilder().sender(sender).message(message).build();
    }

    public abstract int getSender();

    public abstract String getMessage();

    @Override
    public String raw(Tempo tempo) {
        return String.format("plineact %s %s", getSender(), getMessage());
    }

}
