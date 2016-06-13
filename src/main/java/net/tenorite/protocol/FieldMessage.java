package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class FieldMessage implements Message {

    public static FieldMessage of(int sender, String update) {
        return new FieldMessageBuilder().sender(sender).update(update).build();
    }

    public abstract int getSender();

    public abstract String getUpdate();

    @Override
    public String raw(Tempo tempo) {
        return String.format("f %s %s", getSender(), getUpdate());
    }

}
