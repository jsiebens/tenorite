package net.tenorite.net;

import akka.actor.Props;
import akka.actor.UntypedActor;
import io.netty.channel.embedded.EmbeddedChannel;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.clients.MessageSink;
import net.tenorite.clients.ClientRegistrationException;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.clients.events.ClientRegistrationFailed;
import net.tenorite.protocol.Inbound;
import net.tenorite.protocol.PlineMessage;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class TetrinetServerHandlerTest extends AbstractActorTestCase {

    private static class ClientStubActor extends UntypedActor {

        private MessageSink channel;

        public ClientStubActor(MessageSink channel) {
            this.channel = channel;
        }

        @Override
        public void preStart() throws Exception {
            channel.write(PlineMessage.of("ok"));
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Inbound) {
                channel.write(PlineMessage.of(((Inbound) message).getMessage().toUpperCase()));
            }
        }

    }

    @Test
    public void testSuccesfulLogin() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> completedFuture(ClientRegistered.of(system.actorOf(Props.create(ClientStubActor.class, channel))));

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("pline 0 ok");
    }

    @Test
    public void testInvalidInitialization() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> completedFuture(ClientRegistered.of(system.actorOf(Props.create(ClientStubActor.class, channel))));

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound("invalidinittoken");

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("noconnecting invalid initialization");
    }

    @Test
    public void testInvalidNickName() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> error(ClientRegistrationFailed.invalidName());

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("noconnecting invalid nickname");
    }

    @Test
    public void testNickNameAlreadyInUse() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> error(ClientRegistrationFailed.nameAlreadyInUse());

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("noconnecting nickname already in use");
    }

    @Test
    public void testNickNameAlreadyInUseWithCompletionException() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> completedError(ClientRegistrationFailed.nameAlreadyInUse());

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("noconnecting nickname already in use");
    }

    @Test
    public void testUknownErrorOccured() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> error(new IllegalStateException());

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));

        String s = waitUntil(channel::readOutbound);

        assertThat(s).isEqualTo("noconnecting an error occured");
    }

    @Test
    public void testSendingMessages() {
        ClientsRegistry clientsRegistry = (tempo, name, channel) -> completedFuture(ClientRegistered.of(system.actorOf(Props.create(ClientStubActor.class, channel))));

        EmbeddedChannel channel = new EmbeddedChannel(new TetrinetServerHandler(clientsRegistry));

        channel.writeInbound(encode(InitTokenDecoder.TETRIFASTER, "junit", "1.13"));
        channel.writeInbound("hello world");
        channel.writeInbound("lorem ipsum");

        //assertThat(waitUntil(channel::readOutbound)).isNotNull();
        assertThat(waitUntil(channel::readOutbound)).isEqualTo("pline 0 ok");
        assertThat(waitUntil(channel::readOutbound)).isEqualTo("pline 0 HELLO WORLD");
        assertThat(waitUntil(channel::readOutbound)).isEqualTo("pline 0 LOREM IPSUM");
    }

    private CompletableFuture<ClientRegistered> error(ClientRegistrationFailed failure) {
        return error(new ClientRegistrationException(failure));
    }

    private CompletableFuture<ClientRegistered> completedError(ClientRegistrationFailed failure) {
        return error(new CompletionException(new ClientRegistrationException(failure)));
    }

    private CompletableFuture<ClientRegistered> error(Exception ex) {
        CompletableFuture<ClientRegistered> result = new CompletableFuture<>();
        result.completeExceptionally(ex);
        return result;
    }

    private String waitUntil(Supplier<String> s) {
        int i = 0;
        String x = null;

        while ((x = s.get()) == null && i < 10) {
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }

        return x;
    }

}
