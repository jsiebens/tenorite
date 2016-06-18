package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static akka.actor.ActorRef.noSender;
import static java.util.Arrays.asList;

public class ClientActorTest extends AbstractActorTestCase {

    private static final Set<PlineMessage> WELCOME_MESSAGES = new HashSet<>(asList(
        PlineMessage.of(""),
        PlineMessage.of("Welcome on Tenorite TetriNET Server!")
    ));

    private static Predicate<Object> ignoreWelcomeMessages() {
        return o -> !WELCOME_MESSAGES.contains(o);
    }

    @Test
    public void testMessagesAreForwaredToMessageSink() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(LvlMessage.class).or(accept(PlineMessage.class).and(ignoreWelcomeMessages())));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.NORMAL, "junit", stub(output), channels.getRef()));

        client.tell(PlineMessage.of("hello world"), noSender());
        client.tell(LvlMessage.of(1, 11), noSender());

        channels.expectNoMsg();
        output.expectMsgAllOf(PlineMessage.of("hello world"), LvlMessage.of(1, 11));
    }

    @Test
    public void testJoinChannel() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(Inbound.of("pline 1 /join channel"), noSender());

        channels.expectMsgClass(ReserveSlot.class);
        output.expectNoMsg();
    }

    @Test
    public void testChannelAlreadyFull() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(SlotReservationFailed.channelIsFull(), noSender());

        channels.expectNoMsg();
        output.expectMsgAllOf(PlineMessage.of("channel is FULL"));
    }

    @Test
    public void testChannelIsNotAvailable() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(SlotReservationFailed.channelNotAvailable(), noSender());

        channels.expectNoMsg();
        output.expectMsgAllOf(PlineMessage.of("channel is not available"));
    }

    @Test
    public void testSlotReserved() {
        JavaTestKit channels = newTestKit();
        JavaTestKit channelA = newTestKit();
        JavaTestKit channelB = newTestKit();

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(SlotReserved.instance(), channelA.getRef());
        client.tell(SlotReserved.instance(), channelB.getRef());
        client.tell(ChannelLeft.of(Tempo.FAST, "channel", "junit"), channelA.getRef());

        channels.expectNoMsg();

        channelA.expectMsgAllOf(ConfirmSlot.instance(), LeaveChannel.instance());
        channelB.expectMsgAllOf(ConfirmSlot.instance());
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
