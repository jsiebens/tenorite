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
package net.tenorite.websocket;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tenorite.channel.actors.ChannelsActors;
import net.tenorite.channel.commands.Spectate;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMessage;
import net.tenorite.protocol.Message;
import net.tenorite.util.AbstractActor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static akka.actor.ActorRef.noSender;
import static akka.actor.PoisonPill.getInstance;
import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
public class SpectateWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final ActorSystem system;

    private final ChannelsActors channels;

    private final Map<String, ActorRef> sessions = new ConcurrentHashMap<>();

    public SpectateWebSocketHandler(ActorSystem system, ChannelsActors channels) {
        this.system = system;
        this.channels = channels;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String tempo = (String) session.getAttributes().get("tempo");
        String channel = (String) session.getAttributes().get("channel");

        ActorRef actor = system.actorOf(Props.create(MonitorWebSocketActor.class, session, mapper), "spectator_" + session.getId());
        channels.get(Tempo.valueOf(tempo)).tell(Spectate.of(channel), actor);
        sessions.put(session.getId(), actor);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ofNullable(sessions.remove(session.getId())).ifPresent(r -> r.tell(getInstance(), noSender()));
    }

    private static class MonitorWebSocketActor extends AbstractActor {

        private WebSocketSession session;

        private ObjectMapper mapper;

        public MonitorWebSocketActor(WebSocketSession session, ObjectMapper mapper) {
            this.session = session;
            this.mapper = mapper;
        }

        @Override
        public void postStop() throws Exception {
            super.postStop();
            if (session.isOpen()) {
                session.close();
            }
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Message) {
                send((Message) message);
            }
        }

        private void send(Message m) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json(GameMessage.of(System.currentTimeMillis(), m))));
                }
            }
            catch (Exception e) {
                // TODO!!
                e.printStackTrace();
            }
        }

        private String json(GameMessage message) throws Exception {
            return mapper.writeValueAsString(message);
        }

    }

}
