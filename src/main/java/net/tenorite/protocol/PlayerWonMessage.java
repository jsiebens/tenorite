package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class PlayerWonMessage implements Message {

    public static PlayerWonMessage of(int sender) {
        return new PlayerWonMessageBuilder().sender(sender).build();
    }

    public abstract int getSender();

    @Override
    public String raw(Tempo tempo) {
        return String.format("playerwon %s", getSender());
    }

}
