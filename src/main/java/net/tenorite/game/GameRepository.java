package net.tenorite.game;

import net.tenorite.core.Tempo;

import java.util.List;
import java.util.Optional;

public interface GameRepository {

    GameOps gameOps(Tempo tempo);

    interface GameOps {

        void saveGame(Game game);

        Optional<Game> loadGame(String id);

        List<Game> recentGames(GameModeId gameModeId);

    }

}
