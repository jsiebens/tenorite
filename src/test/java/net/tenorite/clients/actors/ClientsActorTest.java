package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.clients.MessageSink;
import net.tenorite.clients.commands.RegisterClient;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModes;
import net.tenorite.protocol.Message;
import org.junit.Test;

import java.util.Collections;

public class ClientsActorTest extends AbstractActorTestCase {

    private GameModes gameModes = new GameModes(Collections.emptyList());

    @Test
    public void testClientsActorShouldBlockInvalidNickName() {
        JavaTestKit channels = newTestKit();
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props(Tempo.NORMAL, gameModes, channels.getRef()));

        clients.tell(RegisterClient.of("x", noop()), clientA.getRef());
        clients.tell(RegisterClient.of("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", noop()), clientB.getRef());

        clientA.expectMsgEquals(ClientRegistrationFailed.invalidName());
        clientB.expectMsgEquals(ClientRegistrationFailed.invalidName());
    }

    @Test
    public void testClientsActorShouldBlockAlreadyUsedNicknames() {
        JavaTestKit channels = newTestKit();
        JavaTestKit clientA = newTestKit();
        JavaTestKit clientB = newTestKit();

        ActorRef clients = system.actorOf(ClientsActor.props(Tempo.NORMAL, gameModes, channels.getRef()));

        clients.tell(RegisterClient.of("junit", noop()), clientA.getRef());
        clients.tell(RegisterClient.of("junit", noop()), clientB.getRef());

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
