package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class PlayerLostMessage implements Message {

    public static PlayerLostMessage of(int sender) {
        return new PlayerLostMessageBuilder().sender(sender).build();
    }

    public abstract int getSender();

    @Override
    public String raw(Tempo tempo) {
        return String.format("playerlost %s", getSender());
    }

}
