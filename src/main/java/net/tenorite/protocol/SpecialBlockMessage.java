package net.tenorite.protocol;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class SpecialBlockMessage implements Message {

    public static SpecialBlockMessage of(int sender, Special special, int target) {
        return new SpecialBlockMessageBuilder().sender(sender).special(special).target(target).build();
    }

    public abstract int getSender();

    public abstract Special getSpecial();

    public abstract int getTarget();

    @Override
    public String raw(Tempo tempo) {
        return String.format("sb %s %s %s", getTarget(), getSpecial().getLetter(), getSender());
    }

}
