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

public class ClientActorTest extends AbstractActorTestCase {

    private GameModes gameModes = new GameModes(Collections.emptyList());

    private static final Set<PlineMessage> WELCOME_MESSAGES = new HashSet<>(asList(
        PlineMessage.of(""),
        PlineMessage.of("Welcome on <b>Tenorite TetriNET</b> Server!"),
        PlineMessage.of("<i>Join a channel to start playing...</i>")
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

        channels.expectMsgAllOf(ListChannels.of(Tempo.NORMAL));
        output.expectMsgAllOf(PlineMessage.of("hello world"), LvlMessage.of(1, 11));
    }

    @Test
    public void testJoinChannel() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /join channel"), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST), ReserveSlot.of(Tempo.FAST, "channel", "junit"));
    }

    @Test
    public void testListChannels() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit();

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(Inbound.of("pline 1 /list"), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
    }

    @Test
    public void testChannelAlreadyFull() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(SlotReservationFailed.channelIsFull(), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
        output.expectMsgAllOf(PlineMessage.of("channel is <b>FULL</b>"));
    }

    @Test
    public void testChannelIsNotAvailable() {
        JavaTestKit channels = newTestKit();
        JavaTestKit output = newTestKit(accept(PlineMessage.class).and(ignoreWelcomeMessages()));

        ActorRef client = system.actorOf(ClientActor.props(Tempo.FAST, "junit", stub(output), gameModes, channels.getRef()));

        client.tell(SlotReservationFailed.channelNotAvailable(), noSender());

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));
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

        channels.expectMsgAllOf(ListChannels.of(Tempo.FAST));

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

        Channels channels = Channels.of(Channel.of(Classic.ID, "a"), Channel.of(Jelly.ID, "b"));

        client.tell(channels, noSender());

        output.expectMsgAllOf(
            PlineMessage.of("   a  <blue>(0/6)</blue>"),
            PlineMessage.of("   b<gray> - " + jelly.getDescription(Tempo.FAST) + "</gray>  <blue>(0/6)</blue>"),
            PlineMessage.of("<gray>(type /join <name>)</gray>")
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
