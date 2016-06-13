package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ClassicStyleAddMessage implements Message {

    public static ClassicStyleAddMessage of(int sender, int lines) {
        return new ClassicStyleAddMessageBuilder().sender(sender).lines(lines).build();
    }

    public abstract int getSender();

    public abstract int getLines();

    @Override
    public String raw(Tempo tempo) {
        return String.format("sb 0 cs%s %s", getLines(), getSender());
    }

}
