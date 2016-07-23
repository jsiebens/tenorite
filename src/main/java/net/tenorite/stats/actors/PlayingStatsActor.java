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
package net.tenorite.stats.actors;

import akka.actor.Props;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import net.tenorite.stats.PlayerStatsRepository;
import net.tenorite.util.AbstractActor;

import java.util.List;

/**
 * @author Johan Siebens
 */
public final class PlayingStatsActor extends AbstractActor {

    public static Props props(PlayerStatsRepository playerStatsRepository) {
        return Props.create(PlayingStatsActor.class, playerStatsRepository).withDispatcher("playerstats-dispatcher");
    }

    private final PlayerStatsRepository playerStatsRepository;

    public PlayingStatsActor(PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        subscribe(GameFinished.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameFinished) {
            handleGameFinished((GameFinished) message);
        }
    }

    private void handleGameFinished(GameFinished event) {
        Game game = event.getGame();
        List<PlayingStats> ranking = event.getRanking();

        if (ranking.size() > 1) {
            PlayerStatsRepository.PlayerStatsOps ops = playerStatsRepository.playerStatsOps(game.getTempo());

            ops.updateStats(game.getGameModeId(), ranking.get(0), true);
            ranking.stream().skip(1).forEach(p -> ops.updateStats(game.getGameModeId(), p, false));
        }
    }

}
