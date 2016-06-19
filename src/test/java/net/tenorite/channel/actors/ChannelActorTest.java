package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.Field;
import net.tenorite.game.GameMode;
import net.tenorite.protocol.*;
import org.junit.Test;

public class ChannelActorTest extends AbstractActorTestCase {

    private final static Tempo TEMPO = Tempo.NORMAL;

    @Test
    public void testPlayerShouldReceiveWelcomeMessageWhenJoiningAChannel() {
        JavaTestKit player1 = newTestKit(accept(PlineMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "azerty"));

        joinChannel(player1, "John", channelActor);

        player1.expectMsgAllOf(
            PlineMessage.of(""),
            PlineMessage.of("Hello <b>John</b>, welcome in channel <b>azerty</b>"),
            PlineMessage.of("")
        );
    }

    @Test
    public void testStartPauseResumeStopGame() {
        NewGameMessage newgame = NewGameMessage.of(GameMode.CLASSIC.getGameRules().toString());

        JavaTestKit player1 = newTestKit(accept(NewGameMessage.class).or(accept(GamePausedMessage.class)).or(accept(GameRunningMessage.class)).or(accept(EndGameMessage.class)));
        JavaTestKit player2 = newTestKit(accept(NewGameMessage.class).or(accept(GamePausedMessage.class)).or(accept(GameRunningMessage.class)).or(accept(EndGameMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(PauseGameMessage.of(1), player1.getRef());
        channelActor.tell(ResumeGameMessage.of(1), player1.getRef());
        channelActor.tell(StopGameMessage.of(1), player1.getRef());

        player1.expectMsgAllOf(newgame, GamePausedMessage.of(), GameRunningMessage.of(), EndGameMessage.of());
        player2.expectMsgAllOf(newgame, GamePausedMessage.of(), GameRunningMessage.of(), EndGameMessage.of());
    }

    @Test
    public void testEndGameMessageShouldBeReceivedWhenAllButOnePlayerIsLost() {
        JavaTestKit player1 = newTestKit(accept(PlayerLostMessage.class).or(accept(EndGameMessage.class)));
        JavaTestKit player2 = newTestKit(accept(PlayerLostMessage.class).or(accept(EndGameMessage.class)));
        JavaTestKit player3 = newTestKit(accept(PlayerLostMessage.class).or(accept(EndGameMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);
        joinChannel(player3, "c", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(PlayerLostMessage.of(2), player2.getRef());
        channelActor.tell(PlayerLostMessage.of(3), player3.getRef());

        player1.expectMsgAllOf(PlayerLostMessage.of(2), PlayerLostMessage.of(3), EndGameMessage.of());
        player2.expectMsgAllOf(PlayerLostMessage.of(2), PlayerLostMessage.of(3), EndGameMessage.of());
        player3.expectMsgAllOf(PlayerLostMessage.of(2), PlayerLostMessage.of(3), EndGameMessage.of());
    }

    @Test
    public void testCurrentPlayerShouldReceiveMessageWhenANewPlayerJoined() {
        JavaTestKit player1 = newTestKit(accept(PlayerJoinMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerJoinMessage.class));
        JavaTestKit player3 = newTestKit(accept(PlayerJoinMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);
        joinChannel(player3, "c", channelActor);

        player3.expectMsgAllOf(PlayerJoinMessage.of(1, "a"), PlayerJoinMessage.of(2, "b"));
    }

    @Test
    public void testPlayerShouldReceiveASlotNumberWhenJoiningAChannel() {
        JavaTestKit player1 = newTestKit(accept(PlayerNumMessage.class));
        JavaTestKit player2 = newTestKit(accept(PlayerNumMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "a", channelActor);
        joinChannel(player2, "b", channelActor);

        player1.expectMsgAllOf(PlayerNumMessage.of(1));
        player2.expectMsgAllOf(PlayerNumMessage.of(2));
    }

    @Test
    public void testPlayerShouldReceiveIngameMessageWhenJoiningGameInRunning() {
        JavaTestKit player1 = newTestKit(accept(IngameMessage.class).or(accept(GameRunningMessage.class)));
        JavaTestKit player2 = newTestKit(accept(IngameMessage.class).or(accept(GameRunningMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());

        joinChannel(player2, "jane", channelActor);

        player2.expectMsgAllOf(IngameMessage.of(), GameRunningMessage.of());
    }

    @Test
    public void testPlayerShouldReceiveIngameMessageWhenJoiningGameInRunningButPaused() {
        JavaTestKit player1 = newTestKit(accept(IngameMessage.class).or(accept(GamePausedMessage.class)));
        JavaTestKit player2 = newTestKit(accept(IngameMessage.class).or(accept(GamePausedMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(PauseGameMessage.of(1), player1.getRef());

        joinChannel(player2, "jane", channelActor);

        player2.expectMsgAllOf(IngameMessage.of(), GamePausedMessage.of());
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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

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

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        channelActor.tell(TeamMessage.of(1, "Doe's"), player1.getRef());
        channelActor.tell(TeamMessage.of(2, "Doe's"), player2.getRef());

        player1.expectMsgAllOf(TeamMessage.of(2, "Doe's"));
        player2.expectMsgAllOf(TeamMessage.of(1, ""), TeamMessage.of(1, "Doe's"));
        player3.expectMsgAllOf(TeamMessage.of(1, ""), TeamMessage.of(2, ""), TeamMessage.of(1, "Doe's"), TeamMessage.of(2, "Doe's"));
    }

    @Test
    public void testFieldMessagesAreForwardedOnlyToOtherPlayersAndOnlyWhenGameIsStarted() {
        Field field = Field.randomCompletedField();

        JavaTestKit player1 = newTestKit(accept(FieldMessage.class));
        JavaTestKit player2 = newTestKit(accept(FieldMessage.class));
        JavaTestKit player3 = newTestKit(accept(FieldMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(FieldMessage.of(1, field.getFieldString()), player1.getRef());

        player1.expectNoMsg();
        player2.expectMsgAllOf(FieldMessage.of(1, field.getFieldString()));
        player3.expectMsgAllOf(FieldMessage.of(1, field.getFieldString()));
    }

    @Test
    public void testFieldMessagesAreIgnoredWhenGameIsNotStarted() {
        Field field = Field.randomCompletedField();

        JavaTestKit player1 = newTestKit(accept(FieldMessage.class));
        JavaTestKit player2 = newTestKit(accept(FieldMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);

        channelActor.tell(FieldMessage.of(1, field.getFieldString()), player1.getRef());

        player2.expectNoMsg();
    }

    @Test
    public void testPlayerReceivesFieldWhenJoiningGameInProgress() {
        String field1 = Field.randomCompletedField().getFieldString();
        String field2 = Field.randomCompletedField().getFieldString();

        JavaTestKit player1 = newTestKit(accept(FieldMessage.class));
        JavaTestKit player2 = newTestKit(accept(FieldMessage.class));
        JavaTestKit player3 = newTestKit(accept(FieldMessage.class));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(FieldMessage.of(1, field1), player1.getRef());
        channelActor.tell(FieldMessage.of(2, field2), player2.getRef());

        joinChannel(player3, "nick", channelActor);

        player1.expectMsgAllOf(FieldMessage.of(2, field2));
        player2.expectMsgAllOf(FieldMessage.of(1, field1));
        player3.expectMsgAllOf(FieldMessage.of(1, field1), FieldMessage.of(2, field2));
    }

    @Test
    public void testSpecialBlockMessagesAreForwardedOnlyToOtherPlayersAndOnlyWhenGameIsStarted() {
        JavaTestKit player1 = newTestKit(accept(SpecialBlockMessage.class).or(accept(ClassicStyleAddMessage.class)));
        JavaTestKit player2 = newTestKit(accept(SpecialBlockMessage.class).or(accept(ClassicStyleAddMessage.class)));
        JavaTestKit player3 = newTestKit(accept(SpecialBlockMessage.class).or(accept(ClassicStyleAddMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);
        joinChannel(player3, "nick", channelActor);

        channelActor.tell(StartGameMessage.of(1), player1.getRef());
        channelActor.tell(SpecialBlockMessage.of(1, Special.ADDLINE, 2), player1.getRef());
        channelActor.tell(ClassicStyleAddMessage.of(1, 4), player1.getRef());

        player1.expectNoMsg();
        player2.expectMsgAllOf(SpecialBlockMessage.of(1, Special.ADDLINE, 2), ClassicStyleAddMessage.of(1, 4));
        player3.expectMsgAllOf(SpecialBlockMessage.of(1, Special.ADDLINE, 2), ClassicStyleAddMessage.of(1, 4));
    }

    @Test
    public void testSpecialBlockMessagesAreIgnoredWhenGameIsNotStarted() {
        JavaTestKit player1 = newTestKit(accept(SpecialBlockMessage.class).or(accept(ClassicStyleAddMessage.class)));
        JavaTestKit player2 = newTestKit(accept(SpecialBlockMessage.class).or(accept(ClassicStyleAddMessage.class)));

        ActorRef channelActor = system.actorOf(ChannelActor.props(TEMPO, GameMode.CLASSIC, "channel"));

        joinChannel(player1, "john", channelActor);
        joinChannel(player2, "jane", channelActor);

        channelActor.tell(SpecialBlockMessage.of(1, Special.ADDLINE, 2), player1.getRef());
        channelActor.tell(ClassicStyleAddMessage.of(1, 4), player1.getRef());

        player2.expectNoMsg();
    }

    private void joinChannel(JavaTestKit player, String name, ActorRef channel) {
        channel.tell(ReserveSlot.of(Tempo.NORMAL, "tetrinet", name), player.getRef());
        channel.tell(ConfirmSlot.instance(), player.getRef());
    }

}
