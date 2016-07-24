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
package net.tenorite.badges.repository;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
public class MongoBadgeRepository implements BadgeRepository {

    private Jongo jongo;

    private Map<Tempo, MongoCollection> badgeCollections = new EnumMap<>(Tempo.class);

    private Map<Tempo, MongoCollection> progressCollections = new EnumMap<>(Tempo.class);

    public MongoBadgeRepository(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public BadgeOps badgeOps(Tempo tempo) {
        MongoCollection badges = badgeCollections.computeIfAbsent(tempo, t -> badgeCollection(jongo, t));
        MongoCollection data = progressCollections.computeIfAbsent(tempo, t -> progressCollection(jongo, t));
        return new MongoBadgeOps(badges, data);
    }

    private class MongoBadgeOps implements BadgeOps {

        private MongoCollection badges;

        private MongoCollection data;

        public MongoBadgeOps(MongoCollection badges, MongoCollection data) {
            this.badges = badges;
            this.data = data;
        }

        @Override
        public Optional<BadgeLevel> getBadgeLevel(String name, Badge badge) {
            return ofNullable(badges.findOne("{badge:#, name:#}", badge, name).as(BadgeLevel.class));
        }

        @Override
        public void saveBadgeLevel(BadgeLevel badgeLevel) {
            badges
                .findAndModify("{badge:#, name:#}", badgeLevel.getBadge(), badgeLevel.getName())
                .with("{$set: #}", badgeLevel)
                .upsert()
                .as(BadgeLevel.class);
        }

        @Override
        public long getProgress(Badge badge, String name) {
            return
                ofNullable(data
                    .findOne("{badge:#, name:#}", badge, name)
                    .projection("{value:1}").as(Value.class)
                )
                    .map(v -> v.value)
                    .orElse(0);
        }

        @Override
        public long updateProgress(Badge badge, String name, long value) {
            if (value == 0) {
                data.remove("{badge:#, name:#}", badge, name);
                return 0;
            }
            else {
                data
                    .findAndModify("{badge:#, name:#}", badge, name)
                    .with("{$set: {value:#}}", value)
                    .upsert()
                    .as(Object.class);
                return value;
            }
        }

        @Override
        public Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name) {
            MongoCursor<BadgeLevel> cursor = badges.find("{badge.gameModeId:#, name:#}", gameModeId, name).as(BadgeLevel.class);
            return stream(cursor.spliterator(), false).collect(Collectors.toMap(BadgeLevel::getBadge, Function.identity()));
        }

        @Override
        public List<BadgeLevel> badgeLevels(Badge badge) {
            MongoCursor<BadgeLevel> cursor = badges.find("{badge:#}", badge).sort("{level:-1,timestamp:1}").limit(20).as(BadgeLevel.class);
            return stream(cursor.spliterator(), false).collect(toList());
        }

    }

    private static class Value {

        int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }

    static MongoCollection badgeCollection(Jongo jongo, Tempo tempo) {
        return jongo.getCollection(tempo + ":player:badges");
    }

    static MongoCollection progressCollection(Jongo jongo, Tempo tempo) {
        return jongo.getCollection(tempo + ":player:badges:progress");
    }

}
