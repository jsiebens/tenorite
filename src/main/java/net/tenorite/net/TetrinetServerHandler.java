/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.net;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tenorite.clients.ClientRegistrationException;
import net.tenorite.clients.ClientsRegistry;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Inbound;
import net.tenorite.protocol.Message;
import net.tenorite.util.Style;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletionException;

import static akka.actor.ActorRef.noSender;
import static net.tenorite.net.InitTokenDecoder.TETRIFASTER;
import static net.tenorite.net.InitTokenDecoder.decode;

/**
 * @author Johan Siebens
 */
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
                String raw = message.raw(tempo);
                if (raw.startsWith("pline ")) {
                    ctx.writeAndFlush(Style.apply(raw));
                }
                else {
                    ctx.writeAndFlush(raw);
                }
            }

            @Override
            public void close() {
                ctx.close();
            }

        };
    }

}
