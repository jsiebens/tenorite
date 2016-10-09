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
package net.tenorite.stats.repository;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.game.PlayingStats;
import net.tenorite.stats.PlayerStats;
import net.tenorite.stats.PlayerStatsRepository;
import net.tenorite.system.config.MongoCollections;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;

/**
 * @author Johan Siebens
 */
@Component
public final class MongoPlayerStatsRepository implements PlayerStatsRepository {

    private final Map<Tempo, PlayerStatsOps> ops = new EnumMap<>(Tempo.class);

    @Autowired
    public MongoPlayerStatsRepository(MongoCollections collections) {
        stream(Tempo.values()).forEach(t -> ops.put(t, createOps(t, collections)));
    }

    @Override
    public PlayerStatsOps playerStatsOps(Tempo tempo) {
        return ops.get(tempo);
    }

    private static PlayerStatsOps createOps(Tempo tempo, MongoCollections collections) {
        return new MongoPlayerStatsOps(collections.getCollection(tempo, "player:stats"));
    }

    private static class MongoPlayerStatsOps implements PlayerStatsOps {

        private final MongoCollection collection;

        MongoPlayerStatsOps(MongoCollection collection) {
            this.collection = collection;
        }

        @Override
        public Optional<PlayerStats> playerStats(GameModeId gameModeId, String name) {
            PlayerStats stats = collection.findOne("{gameModeId:#, name:#}", gameModeId, name).as(PlayerStats.class);
            return Optional.ofNullable(stats);
        }

        @Override
        public void updateStats(GameModeId gameModeId, PlayingStats playingStats, boolean winner) {
            String name = playingStats.getPlayer().getName();

            Map<String, Number> incr = new LinkedHashMap<>();

            incr.put("gamesPlayed", 1);
            incr.put("gamesWon", winner ? 1 : 0);
            incr.put("timePlayed", playingStats.getPlayingTime());
            incr.put("nrOfLines", playingStats.getNrOfLines());
            incr.put("nrOfBlocks", playingStats.getNrOfBlocks());
            incr.put("nrOfTwoLineCombos", playingStats.getNrOfTwoLineCombos());
            incr.put("nrOfThreeLineCombos", playingStats.getNrOfThreeLineCombos());
            incr.put("nrOfFourLineCombos", playingStats.getNrOfFourLineCombos());

            for (Special value : Special.values()) {
                incr.put("nrOfSpecialsOnOpponent." + value, playingStats.getNrOfSpecialsOnOpponent().getOrDefault(value, 0));
                incr.put("nrOfSpecialsOnSelf." + value, playingStats.getNrOfSpecialsOnSelf().getOrDefault(value, 0));
                incr.put("nrOfSpecialsOnTeamPlayer." + value, playingStats.getNrOfSpecialsOnTeamPlayer().getOrDefault(value, 0));
                incr.put("nrOfSpecialsReceived." + value, playingStats.getNrOfSpecialsReceived().getOrDefault(value, 0));
            }

            collection
                .update("{gameModeId:#, name:#}", gameModeId, name)
                .upsert()
                .with("{$inc : #}", incr);
        }

    }

    static MongoCollection createCollection(Jongo jongo, Tempo tempo) {
        return jongo.getCollection(tempo + ":player:stats");
    }

}
