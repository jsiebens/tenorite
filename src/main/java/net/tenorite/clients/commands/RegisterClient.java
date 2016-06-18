package net.tenorite.clients.commands;

import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class RegisterClient {

    public static RegisterClient of(Tempo tempo, String name, MessageSink channel) {
        return new RegisterClientBuilder().tempo(tempo).name(name).channel(channel).build();
    }

    public abstract Tempo getTempo();

    public abstract String getName();

    public abstract MessageSink getChannel();

}
