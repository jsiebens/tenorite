package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class PlineMessage implements Message {

    public static PlineMessage of(String message) {
        return PlineMessage.of(0, message);
    }

    public static PlineMessage of(int sender, String message) {
        return new PlineMessageBuilder().sender(sender).message(message).build();
    }

    public abstract int getSender();

    public abstract String getMessage();

    @Override
    public String raw(Tempo tempo) {
        return String.format("pline %s %s", getSender(), getMessage());
    }

}
