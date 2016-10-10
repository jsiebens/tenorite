package net.tenorite.tournament.events;

import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.tournament.TournamentMatch;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class TournamentGameFinished {

    public static TournamentGameFinished of(TournamentMatch match, Game game, List<PlayingStats> ranking) {
        return new TournamentGameFinishedBuilder().tournamentMatch(match).game(game).ranking(ranking).build();
    }

    public abstract TournamentMatch getTournamentMatch();

    public abstract Game getGame();

    public abstract List<PlayingStats> getRanking();

}
