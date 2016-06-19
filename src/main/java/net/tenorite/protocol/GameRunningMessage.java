package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class GameRunningMessage implements Message {

    public static GameRunningMessage of() {
        return GameRunningMessageBuilder.ImmutableGameRunningMessage.of();
    }

    @Override
    public String raw(Tempo tempo) {
        return "pause 0";
    }

}
