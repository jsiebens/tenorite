package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ResumeGameMessage implements Message {

    public static ResumeGameMessage of(int sender) {
        return new ResumeGameMessageBuilder().sender(sender).build();
    }

    public abstract int getSender();

    @Override
    public String raw(Tempo tempo) {
        return String.format("pause 0 %s", getSender());
    }

}
