package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.MessageParser;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class GameMessage {

    @JsonCreator
    public static GameMessage of(@JsonProperty("timestamp") long timestamp, @JsonProperty("message") String rawMessage) {
        return of(timestamp, MessageParser.parse(rawMessage).orElseThrow(IllegalStateException::new));
    }

    public static GameMessage of(long timestamp, Message message) {
        return new GameMessageBuilder().timestamp(timestamp).message(message).build();
    }

    public abstract long getTimestamp();

    @JsonIgnore
    public abstract Message getMessage();

    @JsonProperty("message")
    private String raw() {
        return getMessage().raw(Tempo.NORMAL);
    }

}
