package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class PlayerNumMessage implements Message {

    public static PlayerNumMessage of(int slot) {
        return new PlayerNumMessageBuilder().slot(slot).build();
    }

    public abstract int getSlot();

    @Override
    public String raw(Tempo tempo) {
        return tempo.equals(Tempo.NORMAL) ? ("playernum " + getSlot()) : (")#)(!@(*3 " + getSlot());
    }

}
