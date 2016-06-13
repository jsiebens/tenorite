package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class StartGameMessage implements Message {

    public static StartGameMessage of(int sender) {
        return new StartGameMessageBuilder().sender(sender).build();
    }

    public abstract int getSender();

    @Override
    public String raw(Tempo tempo) {
        return String.format("startgame 1 %s", getSender());
    }

}
