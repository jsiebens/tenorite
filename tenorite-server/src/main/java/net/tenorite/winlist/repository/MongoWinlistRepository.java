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
package net.tenorite.winlist.repository;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.winlist.WinlistItem;
import net.tenorite.winlist.WinlistRepository;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
public class MongoWinlistRepository implements WinlistRepository {

    static final long MONTH = 1000L * 60L * 60L * 24L * 30L;

    private Jongo jongo;

    private Map<Tempo, MongoCollection> collections = new EnumMap<>(Tempo.class);

    public MongoWinlistRepository(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public WinlistOps winlistOps(Tempo tempo) {
        return new MongoWinlistOps(collections.computeIfAbsent(tempo, t -> createCollection(jongo, t)));
    }

    private class MongoWinlistOps implements WinlistOps {

        private MongoCollection collection;

        MongoWinlistOps(MongoCollection collection) {
            this.collection = collection;
        }

        @Override
        public Optional<WinlistItem> getWinlistItem(GameModeId mode, WinlistItem.Type type, String name) {
            return ofNullable(collection.findOne("{mode:#, type:#, name:#}", mode, type, name).as(WinlistItem.class));
        }

        @Override
        public void saveWinlistItem(GameModeId mode, WinlistItem item) {
            collection
                .findAndModify("{mode:#, type:#, name:#}", mode, item.getType(), item.getName())
                .with("{$set: {score:#, timestamp:#}}", item.getScore(), item.getTimestamp())
                .upsert()
                .as(WinlistItem.class);
        }

        @Override
        public List<WinlistItem> loadWinlist(GameModeId mode) {
            long timestamp = System.currentTimeMillis() - MONTH;

            MongoCursor<WinlistItem> as =
                collection.find("{mode:#, timestamp:{$gte:#}}", mode, timestamp)
                    .sort("{score:-1}")
                    .limit(20)
                    .as(WinlistItem.class);

            return stream(as.spliterator(), false).collect(toList());
        }

    }

    static MongoCollection createCollection(Jongo jongo, Tempo tempo) {
        return jongo.getCollection(tempo + ":winlist");
    }

}
