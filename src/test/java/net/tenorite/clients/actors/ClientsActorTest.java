package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.clients.MessageSink;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import org.junit.Test;

public class ClientsActorTest extends AbstractActorTestCase {

    @Test
    public void testClientsActorShouldBlockInvalidNickName() {
        JavaTestKit channels = newTestKit();
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props(channels.getRef()));

        clients.tell(RegisterClient.of(Tempo.NORMAL, "x", noop()), clientA.getRef());
        clients.tell(RegisterClient.of(Tempo.NORMAL, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", noop()), clientB.getRef());

        clientA.expectMsgEquals(ClientRegistrationFailed.invalidName());
        clientB.expectMsgEquals(ClientRegistrationFailed.invalidName());
    }

    @Test
    public void testClientsActorShouldBlockAlreadyUsedNicknames() {
        JavaTestKit channels = newTestKit();
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props(channels.getRef()));

        clients.tell(RegisterClient.of(Tempo.NORMAL, "junit", noop()), clientA.getRef());
        clients.tell(RegisterClient.of(Tempo.NORMAL, "junit", noop()), clientB.getRef());

        clientA.expectMsgClass(ClientRegistered.class);
        clientB.expectMsgEquals(ClientRegistrationFailed.nameAlreadyInUse());
    }

    public MessageSink noop() {
        return new MessageSink() {

            @Override
            public void write(Message message) {

            }

            @Override
            public void close() {

            }

        };
    }

}
