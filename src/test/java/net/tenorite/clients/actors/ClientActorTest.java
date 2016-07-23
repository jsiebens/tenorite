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
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.*;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModes;
import net.tenorite.modes.Jelly;
import net.tenorite.modes.classic.Classic;
import net.tenorite.protocol.Inbound;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlineMessage;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static akka.actor.ActorRef.noSender;
import static java.util.Arrays.asList;

/**
 * @author Johan Siebens
 */
public class ClientActorTest extends AbstractActorTestCase {

    private GameModes gameModes = new GameModes(Collections.emptyList());

    private static final Set<PlineMessage> WELCOME_MESSAGES = new HashSet<>(asList(
        PlineMessage.of(""),
        PlineMessage.of("Welcome on <b>Tenorite TetriNET</b> Server!"),
        PlineMessage.of("<i>Join or create a channel to start playing...</i>")
    ));

    private static Predicate<Object> ignoreWelcomeMessages() {
        return o -> !WELCOME_MESSAGES.contains(o);
    }

    @Test
    public void testMessagesAreForwaredToMessageSink() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(LvlMessage.class).or(accept(PlineMessage.class).and(ignoreWelcomeMessages())));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.NORMAL, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(PlineMessage.of("hello world"), noSender());
        client.tell(LvlMessage.of(1, 11), noSender());

        channels.expectMsgAllOf(ListChannels.instance());
        output.expectMsgAllOf(PlineMessage.of("hello world"), LvlMessage.of(1, 11));
    }

    @Test
    public void testJoinChannel() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /join channel"), noSender());

        channels.expectMsgAllOf(ListChannels.instance(), ReserveSlot.of("channel", "junit"));
    }

    @Test
    public void testListChannels() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /list"), noSender());

        channels.expectMsgAllOf(ListChannels.instance());
    }

    @Test
    public void testChannelAlreadyFull() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(SlotReservationFailed.channelIsFull(), noSender());

        channels.expectMsgAllOf(ListChannels.instance());
        output.expectMsgAllOf(PlineMessage.of("channel is <b>FULL</b>"));
    }

    @Test
    public void testChannelIsNotAvailable() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(SlotReservationFailed.channelNotAvailable(), noSender());

        channels.expectMsgAllOf(ListChannels.instance());
        output.expectMsgAllOf(PlineMessage.of("channel is <b>not available</b>"));
    }

    @Test
    public void testSlotReserved() {
        JavaTestKit channels = newTestKit();
        JavaTestKit channelA = newTestKit();
        JavaTestKit channelB = newTestKit();

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(SlotReserved.instance(), channelA.getRef());
        client.tell(SlotReserved.instance(), channelB.getRef());
        client.tell(ChannelLeft.of(Tempo.FAST, "channel", "junit"), channelA.getRef());

        channels.expectMsgAllOf(ListChannels.instance());

        channelA.expectMsgAllOf(ConfirmSlot.instance(), LeaveChannel.instance());
        channelB.expectMsgAllOf(ConfirmSlot.instance());
    }

    @Test
    public void testChannels() {
        Classic classic = new Classic();
        Jelly jelly = new Jelly();

        GameModes gameModes = new GameModes(asList(classic, jelly));
        JavaTestKit channelsKit = newTestKit();

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channelsKit.getRef()));

        Channels channels = Channels.of(Channel.of(Classic.ID, "a", 2), Channel.of(Jelly.ID, "b", 6));

        client.tell(channels, noSender());

        output.expectMsgAllOf(
            PlineMessage.of("<b>channels:</b>"),
            PlineMessage.of("   a<gray> - " + classic.getDescription(Tempo.FAST) + "</gray>  <blue>(2/6)</blue>"),
            PlineMessage.of("   b<gray> - " + jelly.getDescription(Tempo.FAST) + "</gray>  <red>(FULL)</red>"),
            PlineMessage.of("<purple>(type <b>/join <channel name></b> to join a channel)</purple>")
        );
    }

    @Test
    public void testCreateChannel() {
        Classic classic = new Classic();
        Jelly jelly = new Jelly();

        GameModes gameModes = new GameModes(asList(classic, jelly));
        JavaTestKit channels = newTestKit(accept(CreateChannel.class));

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /create CLASSIC junit"), noSender());

        channels.expectMsgAllOf(CreateChannel.of(classic.getId(), "junit", true));
    }

    @Test
    public void testCreateChannelWithInvalidArguments() {
        Classic classic = new Classic();
        Jelly jelly = new Jelly();

        GameModes gameModes = new GameModes(asList(classic, jelly));
        JavaTestKit channels = newTestKit(accept(CreateChannel.class));

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /create CLASSIC junit invalid"), noSender());

        output.expectMsgAllOf(PlineMessage.of("<red>invalid number of arguments</red>"));
        channels.expectNoMsg();
    }

    private MessageSink stub(JavaTestKit kit) {
        return new MessageSink() {

            @Override
            public void write(Message message) {
                kit.getRef().tell(message, noSender());
            }

            @Override
            public void close() {

            }

        };
    }

}
