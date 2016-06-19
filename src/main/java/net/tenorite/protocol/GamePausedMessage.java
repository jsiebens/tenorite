package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class GamePausedMessage implements Message {

    public static GamePausedMessage of() {
        return GamePausedMessageBuilder.ImmutableGamePausedMessage.of();
    }

    @Override
    public String raw(Tempo tempo) {
        return "pause 1";
    }

}
