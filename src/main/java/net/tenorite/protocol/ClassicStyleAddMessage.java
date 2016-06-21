package net.tenorite.protocol;

import com.google.common.base.Preconditions;
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

    @Value.Check
    protected void check() {
        int lines = getLines();
        Preconditions.checkState((lines == 1 || lines == 2 || lines == 4), "invalid value " + lines + " for 'lines', allowed values are 1, 2 or 4");
    }

    @Override
    public String raw(Tempo tempo) {
        return String.format("sb 0 cs%s %s", getLines(), getSender());
    }

}
