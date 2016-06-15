package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.clients.ClientChannel;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import org.junit.Test;

public class ClientsActorTest extends AbstractActorTestCase {

    @Test
    public void testClientsActorShouldBlockInvalidNickName() {
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props());

        clients.tell(RegisterClient.of(Tempo.NORMAL, "x", noop()), clientA.getRef());
        clients.tell(RegisterClient.of(Tempo.NORMAL, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", noop()), clientB.getRef());

        clientA.expectMsgEquals(ClientRegistrationFailed.invalidName());
        clientB.expectMsgEquals(ClientRegistrationFailed.invalidName());
    }

    @Test
    public void testClientsActorShouldBlockAlreadyUsedNicknames() {
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props());

        clients.tell(RegisterClient.of(Tempo.NORMAL, "junit", noop()), clientA.getRef());
        clients.tell(RegisterClient.of(Tempo.NORMAL, "junit", noop()), clientB.getRef());

        clientA.expectMsgClass(ClientRegistered.class);
        clientB.expectMsgEquals(ClientRegistrationFailed.nameAlreadyInUse());
    }

    public ClientChannel noop() {
        return new ClientChannel() {

            @Override
            public void write(Message message) {

            }

            @Override
            public void close() {

            }

        };
    }

}
