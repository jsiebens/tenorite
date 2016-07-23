package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class StopGameMessage implements Message {

    public static StopGameMessage of(int sender) {
        return new StopGameMessageBuilder().sender(sender).build();
    }

    public abstract int getSender();

    @Override
    public String raw(Tempo tempo) {
        return String.format("startgame 0 %s", getSender());
    }

}
