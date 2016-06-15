package net.tenorite.clients.commands;

import net.tenorite.clients.ClientChannel;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class RegisterClient {

    public static RegisterClient of(Tempo tempo, String name, ClientChannel channel) {
        return new RegisterClientBuilder().tempo(tempo).name(name).channel(channel).build();
    }

    public abstract Tempo getTempo();

    public abstract String getName();

    public abstract ClientChannel getChannel();

}
