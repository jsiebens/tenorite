package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class NewGameMessage implements Message {

    public static NewGameMessage of(String settings) {
        return new NewGameMessageBuilder().settings(settings).build();
    }

    public abstract String getSettings();

    @Override
    public String raw(Tempo tempo) {
        return tempo.equals(Tempo.NORMAL) ? ("newgame " + getSettings()) : ("******* " + getSettings());
    }

}
