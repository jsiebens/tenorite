package net.tenorite.stats.repository;

import net.tenorite.AbstractTenoriteServerTestCase;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.stats.PlayerStats;
import net.tenorite.stats.PlayerStatsBuilder;
import net.tenorite.stats.PlayerStatsRepository;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoPlayerStatsRepositoryTest extends AbstractTenoriteServerTestCase {

    @Autowired
    private Jongo jongo;

    @Autowired
    private PlayerStatsRepository playerStatsRepository;

    @Before
    public void clear() {
        stream(Tempo.values()).forEach(t -> MongoPlayerStatsRepository.createCollection(jongo, t).drop());
    }

    @Test
    public void testUpdateAndGetPlayerStats() {
        PlayingStats playingStats = PlayingStats.of(Player.of(1, "john", null), b -> b
            .playingTime(42)
            .nrOfLines(15)
            .nrOfBlocks(31)
            .nrOfFourLineCombos(1)
            .nrOfThreeLineCombos(3)
            .nrOfTwoLineCombos(5)
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 5)
            .putNrOfSpecialsOnTeamPlayer(Special.CLEARLINE, 9)
            .putNrOfSpecialsOnSelf(Special.GRAVITY, 1)
            .putNrOfSpecialsReceived(Special.BLOCKBOMB, 3)
        );

        GameModeId gameModeId = GameModeId.of("JUNIT");
        playerStatsRepository.playerStatsOps(Tempo.FAST).updateStats(gameModeId, playingStats, false);
        playerStatsRepository.playerStatsOps(Tempo.FAST).updateStats(gameModeId, playingStats, true);

        Optional<PlayerStats> stats = playerStatsRepository.playerStatsOps(Tempo.FAST).playerStats(gameModeId, "john");

        PlayerStats expected = new PlayerStatsBuilder()
            .gameModeId(gameModeId)
            .name("john")
            .gamesPlayed(2)
            .gamesWon(1)
            .timePlayed(84)
            .nrOfLines(30)
            .nrOfBlocks(62)
            .nrOfFourLineCombos(2)
            .nrOfThreeLineCombos(6)
            .nrOfTwoLineCombos(10)
            .putAllNrOfSpecialsOnOpponent(defaultMap(m -> m.put(Special.RANDOMCLEAR, 10)))
            .putAllNrOfSpecialsOnTeamPlayer(defaultMap(m -> m.put(Special.CLEARLINE, 18)))
            .putAllNrOfSpecialsOnSelf(defaultMap(m -> m.put(Special.GRAVITY, 2)))
            .putAllNrOfSpecialsReceived(defaultMap(m -> m.put(Special.BLOCKBOMB, 6)))
            .build();

        assertThat(stats).hasValue(expected);
    }


    private Map<Special, Integer> defaultMap(Consumer<Map<Special, Integer>> c) {
        Map<Special, Integer> map = stream(Special.values()).collect(Collectors.toMap(Function.identity(), s -> 0));
        c.accept(map);
        return map;
    }

}
