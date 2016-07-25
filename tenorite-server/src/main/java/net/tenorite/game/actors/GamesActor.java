/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.game.actors;

import akka.actor.Props;
import net.tenorite.game.Game;
import net.tenorite.game.GameRepository;
import net.tenorite.game.events.GameFinished;
import net.tenorite.util.AbstractActor;

/**
 * @author Johan Siebens
 */
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
