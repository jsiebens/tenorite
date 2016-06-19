package net.tenorite.net;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tenorite.clients.MessageSink;
import net.tenorite.clients.ClientRegistrationException;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Inbound;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlineMessage;
import net.tenorite.util.Style;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletionException;

import static akka.actor.ActorRef.noSender;
import static net.tenorite.net.InitTokenDecoder.TETRIFASTER;
import static net.tenorite.net.InitTokenDecoder.decode;

final class TetrinetServerHandler extends SimpleChannelInboundHandler<String> {

    private final Queue<Inbound> received = new ArrayDeque<>();

    private final ClientsRegistry clientsRegistry;

    private boolean finished;

    private ActorRef client;

    TetrinetServerHandler(ClientsRegistry clientsRegistry) {
        this.clientsRegistry = clientsRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        if (finished) {
            received.add(Inbound.of(message));
            flush();
            return;
        }

        finished = true;

        Optional<String[]> decoded = decode(message).map(s -> s.split(" "));

        if (decoded.isPresent()) {
            String[] initialization = decoded.get();

            Tempo tempo = TETRIFASTER.equalsIgnoreCase(initialization[0]) ? Tempo.FAST : Tempo.NORMAL;
            String name = initialization[1];

            clientsRegistry
                .registerClient(tempo, name, clientChannel(tempo, ctx))
                .whenCompleteAsync((result, failure) -> {
                    if (failure != null) {
                        if (failure instanceof CompletionException) {
                            failure = failure.getCause();
                        }

                        if (failure instanceof ClientRegistrationException) {
                            ClientRegistrationException cre = (ClientRegistrationException) failure;
                            switch (cre.getType()) {
                                case INVALID_NAME:
                                    ctx.writeAndFlush(("noconnecting invalid nickname"));
                                    break;
                                case NAME_ALREADY_IN_USE:
                                    ctx.writeAndFlush(("noconnecting nickname already in use"));
                                    break;
                            }
                        }
                        else {
                            ctx.writeAndFlush(("noconnecting an error occured"));
                            ctx.close();
                        }
                    }
                    else {
                        client = result.getClient();
                        ctx.channel().closeFuture().addListener(f -> client.tell(PoisonPill.getInstance(), noSender()));
                        flush();
                    }
                }, ctx.executor());
        }
        else {
            ctx.writeAndFlush(("noconnecting invalid initialization"));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    private void flush() {
        if (client != null) {
            for (; ; ) {
                Inbound msg = received.poll();
                if (msg == null) {
                    break;
                }
                client.tell(msg, noSender());
            }
        }
    }

    private MessageSink clientChannel(Tempo tempo, ChannelHandlerContext ctx) {
        return new MessageSink() {

            @Override
            public void write(Message message) {
                if (message instanceof PlineMessage) {
                    ctx.writeAndFlush(Style.apply(message.raw(tempo)));
                }
                else {
                    ctx.writeAndFlush(message.raw(tempo));
                }
            }

            @Override
            public void close() {
                ctx.close();
            }

        };
    }

}
