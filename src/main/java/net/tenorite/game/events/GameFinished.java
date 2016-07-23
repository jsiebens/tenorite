package net.tenorite.game.events;

import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class GameFinished {

    public static GameFinished of(Game game, List<PlayingStats> ranking) {
        return new GameFinishedBuilder().game(game).ranking(ranking).build();
    }

    public abstract Game getGame();

    public abstract List<PlayingStats> getRanking();

}
