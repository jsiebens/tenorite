package net.tenorite.game;

import net.tenorite.protocol.Message;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class GameMessage {

    public static GameMessage of(long timestamp, Message message) {
        return new GameMessageBuilder().timestamp(timestamp).message(message).build();
    }

    public abstract long getTimestamp();

    public abstract Message getMessage();

}
