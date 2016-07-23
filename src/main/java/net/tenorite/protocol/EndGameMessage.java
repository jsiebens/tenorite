package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class EndGameMessage implements Message {

    public static EndGameMessage of() {
        return EndGameMessageBuilder.ImmutableEndGameMessage.of();
    }

    public String raw(Tempo tempo) {
        return "endgame";
    }

}
