package net.tenorite.stats.repository;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.game.PlayingStats;
import net.tenorite.stats.PlayerStats;
import net.tenorite.stats.PlayerStatsRepository;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class MongoPlayerStatsRepository implements PlayerStatsRepository {

    private final Jongo jongo;

    private final Map<Tempo, MongoCollection> collections = new EnumMap<>(Tempo.class);

    public MongoPlayerStatsRepository(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public PlayerStatsOps playerStatsOps(Tempo tempo) {
        return new MongoPlayerStatsOps(collections.computeIfAbsent(tempo, t -> createCollection(jongo, t)));
    }

    private static class MongoPlayerStatsOps implements PlayerStatsOps {

        private final MongoCollection collection;

        public MongoPlayerStatsOps(MongoCollection collection) {
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
