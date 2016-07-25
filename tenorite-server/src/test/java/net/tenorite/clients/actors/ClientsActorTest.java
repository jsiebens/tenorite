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

/**
 * @author Johan Siebens
 */
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
