package net.tenorite.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.Game;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = TournamentMatchBuilder.ImmutableTournamentMatch.class)
public abstract class TournamentMatch {

    public enum State {
        BLOCKED,
        SCHEDULED,
        FINISHED
    }

    public static TournamentMatch of(String id, String tournament, int round, GameModeId gameModeId, String playerA, String playerB) {
        return new TournamentMatchBuilder()
            .id(id)
            .tournament(tournament)
            .round(round)
            .gameModeId(gameModeId)
            .nrOfGamesToWin(3)
            .addPlayers(Score.of(playerA, 0))
            .addPlayers(Score.of(playerB, 0))
            .state(State.BLOCKED)
            .build();
    }

    @JsonProperty("_id")
    public abstract String getId();

    public abstract String getTournament();

    public abstract int getRound();

    public abstract GameModeId getGameModeId();

    public abstract int getNrOfGamesToWin();

    public abstract List<Score> getPlayers();

    public abstract List<String> getGames();

    public abstract State getState();

    public final boolean hasPlayer(String name) {
        return getPlayers().stream().filter(s -> s.getName().equals(name)).findFirst().isPresent();
    }

    public final TournamentMatch incrScore(Game game, String name) {
        if (hasPlayer(name)) {
            List<Score> newScores = getPlayers().stream().map(s -> s.getName().equals(name) ? s.incr() : s).collect(Collectors.toList());
            boolean hasWinner = newScores.stream().filter(s -> s.getScore() >= getNrOfGamesToWin()).findAny().isPresent();
            State newState = hasWinner ? State.FINISHED : getState();

            return
                new TournamentMatchBuilder()
                    .from(this)
                    .addGames(game.getId())
                    .players(newScores)
                    .state(newState)
                    .build();
        }
        else {
            return this;
        }
    }

}
