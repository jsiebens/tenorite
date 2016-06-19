package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class IngameMessage implements Message {

    public static IngameMessage of() {
        return IngameMessageBuilder.ImmutableIngameMessage.of();
    }

    public String raw(Tempo tempo) {
        return "ingame";
    }

}
