package net.tenorite.game.actors;

import akka.actor.Props;
import net.tenorite.game.Game;
import net.tenorite.game.GameRepository;
import net.tenorite.game.events.GameFinished;
import net.tenorite.util.AbstractActor;

public final class GamesActor extends AbstractActor {

    public static Props props(GameRepository repository) {
        return Props.create(GamesActor.class, repository).withDispatcher("games-dispatcher");
    }

    private final GameRepository gameRepository;

    public GamesActor(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void preStart() throws Exception {
        subscribe(GameFinished.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameFinished) {
            GameFinished gf = (GameFinished) message;
            if (gf.getRanking().size() > 1) {
                Game game = gf.getGame();
                gameRepository.gameOps(game.getTempo()).saveGame(game);
            }
        }
    }

}
