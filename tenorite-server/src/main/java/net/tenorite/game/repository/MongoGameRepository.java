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
package net.tenorite.game.repository;

import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRepository;
import net.tenorite.system.config.MongoCollections;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
@Component
public final class MongoGameRepository implements GameRepository {

    private final Map<Tempo, GameOps> ops = new EnumMap<>(Tempo.class);

    @Autowired
    public MongoGameRepository(MongoCollections collections) {
        stream(Tempo.values()).forEach(t -> ops.put(t, createOps(t, collections)));
    }

    @Override
    public GameOps gameOps(Tempo tempo) {
        return ops.get(tempo);
    }

    private static GameOps createOps(Tempo tempo, MongoCollections collections) {
        return new MongoGameOps(collections.getCollection(tempo, "games"));
    }

    private static class MongoGameOps implements GameOps {

        private MongoCollection collection;

        MongoGameOps(MongoCollection collection) {
            this.collection = collection;
        }

        @Override
        public void saveGame(Game game) {
            collection.save(game);
        }

        @Override
        public Optional<Game> loadGame(String id) {
            return Optional.ofNullable(collection.findOne("{_id:#}", id).as(Game.class));
        }

        @Override
        public List<Game> recentGames(GameModeId gameModeId) {
            MongoCursor<Game> cursor = collection.find("{gameModeId:#}", gameModeId).sort("{timestamp:-1}").limit(10).projection("{messages:0}").as(Game.class);
            return stream(cursor.spliterator(), false).collect(toList());
        }

    }

}
