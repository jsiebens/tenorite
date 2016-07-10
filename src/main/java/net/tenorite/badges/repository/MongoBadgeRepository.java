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
            return ofNullable(badges.findOne("{gameModeId:#, badgeType:#, name:#}", badge.getGameModeId(), badge.getBadgeType(), name).as(BadgeLevel.class));
        }

        @Override
        public void saveBadgeLevel(BadgeLevel badge) {
            badges
                .findAndModify("{gameModeId:#, badgeType:#, name:#}", badge.getGameModeId(), badge.getBadgeType(), badge.getName())
                .with("{$set: #}", badge)
                .upsert()
                .as(BadgeLevel.class);
        }

        @Override
        public long getProgress(Badge type, String name) {
            return
                ofNullable(data
                    .findOne("{gameModeId:#, badgeType:#, name:#}", type.getGameModeId(), type.getBadgeType(), name)
                    .projection("{value:1}").as(Value.class)
                )
                    .map(v -> v.value)
                    .orElse(0);
        }

        @Override
        public long updateProgress(Badge badge, String name, long value) {
            if (value == 0) {
                data.remove("{gameModeId:#, badgeType:#, name:#}", badge.getGameModeId(), badge.getBadgeType(), name);
                return 0;
            }
            else {
                data
                    .findAndModify("{gameModeId:#, badgeType:#, name:#}", badge.getGameModeId(), badge.getBadgeType(), name)
                    .with("{$set: {value:#}}", value)
                    .upsert()
                    .as(Object.class);
                return value;
            }
        }

        @Override
        public Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name) {
            MongoCursor<BadgeLevel> cursor = badges.find("{gameModeId:#, name:#}", gameModeId, name).as(BadgeLevel.class);
            return stream(cursor.spliterator(), false).collect(Collectors.toMap(bl -> Badge.of(bl.getGameModeId(), bl.getBadgeType()), Function.identity()));
        }

        @Override
        public List<BadgeLevel> badgeLevels(Badge badge) {
            MongoCursor<BadgeLevel> cursor = badges.find("{gameModeId:#, badgeType:#}", badge.getGameModeId(), badge.getBadgeType()).sort("{level:-1,timestamp:1}").limit(20).as(BadgeLevel.class);
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
        return jongo.getCollection(tempo + "::player:badges:progress");
    }

}
