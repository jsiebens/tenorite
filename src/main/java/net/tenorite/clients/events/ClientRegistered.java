package net.tenorite.clients.events;

import akka.actor.ActorRef;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ClientRegistered {

    public static ClientRegistered of(ActorRef client) {
        return new ClientRegisteredBuilder().client(client).build();
    }

    public abstract ActorRef getClient();

}
