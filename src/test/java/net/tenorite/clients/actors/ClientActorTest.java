package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Inbound;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlineMessage;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static akka.actor.ActorRef.noSender;
import static java.util.Arrays.asList;

public class ClientActorTest extends AbstractActorTestCase {

    private static final Set<PlineMessage> WELCOME_MESSAGES = new HashSet<>(asList(
        PlineMessage.of(""),
        PlineMessage.of("Welcome on Tenorite TetriNET Server!"),
        PlineMessage.of("Join a channel to start playing...")
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

        channels.expectMsgAllOf(ListChannels.of(Tempo.NORMAL));
        output.expectMsgAllOf(PlineMessage.of("hello world"), LvlMessage.of(1, 11));
    }

    @Test
    public void testJoinChannel() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(Inbound.of("pline 1 /join channel"), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST), ReserveSlot.of(Tempo.FAST, "channel", "junit"));
    }

    @Test
    public void testListChannels() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(Inbound.of("pline 1 /list"), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
    }

    @Test
    public void testChannelAlreadyFull() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(SlotReservationFailed.channelIsFull(), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
        output.expectMsgAllOf(PlineMessage.of("channel is FULL"));
    }

    @Test
    public void testChannelIsNotAvailable() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channels.getRef()));

        client.tell(SlotReservationFailed.channelNotAvailable(), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
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

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));

        channelA.expectMsgAllOf(ConfirmSlot.instance(), LeaveChannel.instance());
        channelB.expectMsgAllOf(ConfirmSlot.instance());
    }

    @Test
    public void testChannels() {
        JavaTestKit channelsKit = newTestKit();

        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), channelsKit.getRef()));

        Channels channels = Channels.of(Channel.of("channel:A"), Channel.of("channel:B"), Channel.of("channel:C"));

        client.tell(channels, noSender());

        output.expectMsgAllOf(
            PlineMessage.of("   channel:A"),
            PlineMessage.of("   channel:B"),
            PlineMessage.of("   channel:C"),
            PlineMessage.of("(type /join <name>)")
        );
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
