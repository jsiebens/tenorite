package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import org.junit.Test;

public class ChannelActorTest extends AbstractActorTestCase {

    private final static Tempo TEMPO = Tempo.NORMAL;

    @Test
    public void testPlayerShouldReceiveWelcomeMessageWhenJoiningAChannel() {
        JavaTestKit player1 = newTestKit(accept(PlineMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "azerty"));

        joinChannel(player1, "John", channelActor);

        player1.expectMsgAllOf(
            PlineMessage.of(""),
            PlineMessage.of("Hello John, welcome in channel azerty"),
            PlineMessage.of("")
        );
    }

    @Test
    public void testCurrentPlayerShouldReceiveMessageWhenANewPlayerJoined() {
        JavaTestKit player1 = newTestKit(accept(PlayerJoinMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerJoinMessage.class));
        JavaTestKit player3 = newTestKit(accept(PlayerJoinMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);
        joinChannel(player3, "c", channelActor);

        player1.expectMsgAllOf(PlayerJoinMessage.of(2, "b"), PlayerJoinMessage.of(3, "c"));
        player2.expectMsgAllOf(PlayerJoinMessage.of(1, "a"), PlayerJoinMessage.of(3, "c"));
        player3.expectMsgAllOf(PlayerJoinMessage.of(1, "a"), PlayerJoinMessage.of(2, "b"));
    }

    @Test
    public void testPlayerShouldReceiveCurrentPlayerListWhenJoiningAChannel() {
        JavaTestKit player1 = newTestKit();
        JavaTestKit player2 = newTestKit();
        JavaTestKit player3 = newTestKit(accept(PlayerJoinMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);
        joinChannel(player3, "c", channelActor);

        player3.expectMsgAllOf(PlayerJoinMessage.of(1, "a"), PlayerJoinMessage.of(2, "b"));
    }

    @Test
    public void testPlayerShouldReceiveASlotNumberWhenJoiningAChannel() {
        JavaTestKit player1 = newTestKit(accept(PlayerNumMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerNumMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);

        player1.expectMsgAllOf(PlayerNumMessage.of(1));
        player2.expectMsgAllOf(PlayerNumMessage.of(2));
    }

    @Test
    public void testMaximum6PlayerCanJoinChannel() {
        JavaTestKit player1 = newTestKit();
        JavaTestKit player2 = newTestKit();
        JavaTestKit player3 = newTestKit();
        JavaTestKit player4 = newTestKit();
        JavaTestKit player5 = newTestKit();
        JavaTestKit player6 = newTestKit();
        JavaTestKit player7 = newTestKit(accept(SlotReservationFailed.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);
        joinChannel(player3, "c", channelActor);
        joinChannel(player4, "d", channelActor);
        joinChannel(player5, "e", channelActor);
        joinChannel(player6, "f", channelActor);

        channelActor.tell(ReserveSlot.of(Tempo.NORMAL, "tetrinet", "g"), player7.getRef());

        player7.expectMsgAllOf(SlotReservationFailed.channelIsFull());
    }

    @Test
    public void testAnnouncePlayerLeftChannel() {
        JavaTestKit player1 = newTestKit(accept(PlayerLeaveMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerLeaveMessage.class));
        JavaTestKit player3 = newTestKit(accept(PlayerLeaveMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        channelActor.tell(LeaveChannel.instance(), player3.getRef());

        player1.expectMsgAllOf(PlayerLeaveMessage.of(3));
        player2.expectMsgAllOf(PlayerLeaveMessage.of(3));
        player3.expectMsgAllOf(PlayerLeaveMessage.of(1), PlayerLeaveMessage.of(2), PlayerLeaveMessage.of(3));
    }

    @Test
    public void testAnnouncePlayerLeftChannelWhenDisconnected() {
        JavaTestKit player1 = newTestKit(accept(PlayerLeaveMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerLeaveMessage.class));
        JavaTestKit player3 = newTestKit(accept(PlayerLeaveMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        system.stop(player3.getRef());

        player1.expectMsgAllOf(PlayerLeaveMessage.of(3));
        player2.expectMsgAllOf(PlayerLeaveMessage.of(3));
    }

    @Test
    public void testNewPlayerReceviesTeamMessageWhenJoining() {
        JavaTestKit player1 = newTestKit(accept(TeamMessage.class));
        JavaTestKit player2 = newTestKit(accept(TeamMessage.class));
        JavaTestKit player3 = newTestKit(accept(TeamMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);

        channelActor.tell(TeamMessage.of(1, "Doe's"), player1.getRef());
        channelActor.tell(TeamMessage.of(2, "Doe's"), player2.getRef());

        joinChannel(player3, "nick", channelActor);

        player3.expectMsgAllOf(TeamMessage.of(1, "Doe's"), TeamMessage.of(2, "Doe's"));
    }

    @Test
    public void testPlayerCanClearHisHerTeam() {
        JavaTestKit player1 = newTestKit(accept(TeamMessage.class));
        JavaTestKit player2 = newTestKit(accept(TeamMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);

        channelActor.tell(TeamMessage.of(1, "Doe's"), player1.getRef());
        channelActor.tell(TeamMessage.of(1, ""), player1.getRef());

        player2.expectMsgAllOf(TeamMessage.of(1, "Doe's"), TeamMessage.of(1, ""));
    }

    @Test
    public void testTeamMessageAreForwardedOnlyToOtherPlayers() {
        JavaTestKit player1 = newTestKit(accept(TeamMessage.class));
        JavaTestKit player2 = newTestKit(accept(TeamMessage.class));
        JavaTestKit player3 = newTestKit(accept(TeamMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        channelActor.tell(TeamMessage.of(1, "Doe's"), player1.getRef());
        channelActor.tell(TeamMessage.of(2, "Doe's"), player2.getRef());

        player1.expectMsgAllOf(TeamMessage.of(2, "Doe's"));
        player2.expectMsgAllOf(TeamMessage.of(1, ""), TeamMessage.of(1, "Doe's"));
        player3.expectMsgAllOf(TeamMessage.of(1, ""), TeamMessage.of(2, ""), TeamMessage.of(1, "Doe's"), TeamMessage.of(2, "Doe's"));
    }

    private void joinChannel(JavaTestKit player, String name, ActorRef channel) {
        channel.tell(ReserveSlot.of(Tempo.NORMAL, "tetrinet", name), player.getRef());
        channel.tell(ConfirmSlot.instance(), player.getRef());
    }

}
