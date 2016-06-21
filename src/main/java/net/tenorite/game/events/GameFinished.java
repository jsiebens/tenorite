package net.tenorite.game.events;

import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@ImmutableStyle
public abstract class GameFinished {

    public static GameFinished of(Game game, List<Player> ranking) {
        return new GameFinishedBuilder().game(game).ranking(ranking).build();
    }

    public abstract Game getGame();

    public abstract List<Player> getRanking();

}
