package net.tenorite.clients.commands;

import net.tenorite.clients.MessageSink;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class RegisterClient {

    public static RegisterClient of(String name, MessageSink channel) {
        return new RegisterClientBuilder().name(name).channel(channel).build();
    }

    public abstract String getName();

    public abstract MessageSink getChannel();

}
