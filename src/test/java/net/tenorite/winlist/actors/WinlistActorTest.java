package net.tenorite.winlist.actors;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import net.tenorite.AbstractActorTestCase;
import net.tenorite.channel.events.ChannelJoined;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import net.tenorite.modes.classic.Classic;
import net.tenorite.winlist.WinlistRepositoryStub;
import net.tenorite.winlist.events.WinlistUpdated;
import org.junit.Before;
import org.junit.Test;

import static akka.actor.ActorRef.noSender;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.tenorite.winlist.WinlistItem.Type.PLAYER;
import static net.tenorite.winlist.WinlistItem.Type.TEAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class WinlistActorTest extends AbstractActorTestCase {

    private WinlistRepositoryStub winlistRepository = new WinlistRepositoryStub();

    @Before
    public void setUpDB() {
        this.winlistRepository.clear();
    }

    @Test
    public void testWinlistIsPublishedWhenPlayerJoinedChannel() {
        JavaTestKit subscriber = newTestKit();
        system.eventStream().subscribe(subscriber.getRef(), WinlistUpdated.class);

        ActorRef winlist = system.actorOf(WinlistActor.props(winlistRepository));

        winlist.tell(ChannelJoined.of(Tempo.FAST, Classic.ID, "channel", "john"), noSender());

        subscriber.expectMsgAllOf(WinlistUpdated.of(Tempo.FAST, Classic.ID, emptyList()));
    }

    @Test
    public void testUpdateWinlistWhenGameIsFinished() {
        JavaTestKit subscriber = newTestKit();
        system.eventStream().subscribe(subscriber.getRef(), WinlistUpdated.class);

        ActorRef winlist = system.actorOf(WinlistActor.props(winlistRepository));

        Player playerA = Player.of(1, "john", null);
        Player playerB = Player.of(2, "jane", null);

        Game game = Game.of("lorem", 1000, 2000, Tempo.NORMAL, Classic.ID, asList(playerA, playerB), emptyList());
        GameFinished gf = GameFinished.of(game, asList(PlayingStats.of(playerA), PlayingStats.of(playerB)));

        winlist.tell(gf, noSender());

        subscriber.expectMsgClass(WinlistUpdated.class);

        assertThat(winlistRepository.winlistOps(Tempo.NORMAL).loadWinlist(Classic.ID))
            .hasSize(2)
            .extracting("type", "name").containsExactly(tuple(PLAYER, "john"), tuple(PLAYER, "jane"));
    }

    @Test
    public void testWinlistUpdateWithTeam() {
        JavaTestKit subscriber = newTestKit();
        system.eventStream().subscribe(subscriber.getRef(), WinlistUpdated.class);

        ActorRef winlist = system.actorOf(WinlistActor.props(winlistRepository));

        Player playerA = Player.of(1, "john", "doe");
        Player playerB = Player.of(2, "jane", "doe");
        Player playerC = Player.of(3, "nick", null);

        Game game = Game.of("lorem", 1000, 2000, Tempo.NORMAL, Classic.ID, asList(playerA, playerB, playerC), emptyList());
        GameFinished gf = GameFinished.of(game, asList(PlayingStats.of(playerA), PlayingStats.of(playerB), PlayingStats.of(playerC)));

        winlist.tell(gf, noSender());

        subscriber.expectMsgClass(WinlistUpdated.class);

        assertThat(winlistRepository.winlistOps(Tempo.NORMAL).loadWinlist(Classic.ID))
            .hasSize(2)
            .extracting("type", "name").containsExactly(tuple(TEAM, "doe"), tuple(PLAYER, "nick"));
    }

    @Test
    public void testWinlistUpdateWithMultipleTeams() {
        JavaTestKit subscriber = newTestKit();
        system.eventStream().subscribe(subscriber.getRef(), WinlistUpdated.class);

        ActorRef winlist = system.actorOf(WinlistActor.props(winlistRepository));

        Player playerA = Player.of(1, "john", "team1");
        Player playerB = Player.of(2, "jane", "team1");
        Player playerC = Player.of(3, "nick", "team2");
        Player playerD = Player.of(4, "solo", null);
        Player playerE = Player.of(5, "olivia", "team2");

        Game game = Game.of("lorem", 1000, 2000, Tempo.NORMAL, Classic.ID, asList(playerA, playerB, playerC, playerD, playerE), emptyList());
        GameFinished gf = GameFinished.of(game, asList(
            PlayingStats.of(playerA),
            PlayingStats.of(playerB),
            PlayingStats.of(playerC),
            PlayingStats.of(playerD),
            PlayingStats.of(playerE)
        ));

        winlist.tell(gf, noSender());

        subscriber.expectMsgClass(WinlistUpdated.class);

        assertThat(winlistRepository.winlistOps(Tempo.NORMAL).loadWinlist(Classic.ID))
            .hasSize(3)
            .extracting("type", "name").containsExactly(tuple(TEAM, "team1"), tuple(TEAM, "team2"), tuple(PLAYER, "solo"));
    }

}
