package net.tenorite.game.repository;

import net.tenorite.AbstractTenoriteServerTestCase;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.modes.Classic;
import net.tenorite.protocol.ClassicStyleAddMessage;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.SpecialBlockMessage;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoGameRepositoryTest extends AbstractTenoriteServerTestCase {

    @Autowired
    private Jongo jongo;

    @Autowired
    private GameRepository gameRepository;

    @Before
    public void clear() {
        stream(Tempo.values()).forEach(t -> MongoGameRepository.createCollection(jongo, t).drop());
    }

    @Test
    public void testSaveAndLoadGame() {
        Game game =
            Game.of("game1", 1000, 250, Tempo.NORMAL, Classic.ID,
                asList(
                    Player.of(1, "john", "doe"),
                    Player.of(2, "jane", "doe"),
                    Player.of(3, "nick", null)
                ),
                asList(
                    GameMessage.of(50, SpecialBlockMessage.of(1, Special.ADDLINE, 3)),
                    GameMessage.of(75, ClassicStyleAddMessage.of(2, 4)),
                    GameMessage.of(100, LvlMessage.of(2, 4))
                )
            );

        gameRepository.gameOps(Tempo.NORMAL).saveGame(game);
        assertThat(gameRepository.gameOps(Tempo.NORMAL).loadGame("game1")).hasValue(game);
    }

    @Test
    public void testListRecentGamesIsLimitedToTenGames() {
        range(1, 31)
            .mapToObj(i -> Game.of("id" + i, i, 10, Tempo.NORMAL, Classic.ID, Collections.emptyList(), Collections.emptyList()))
            .forEach(g -> gameRepository.gameOps(Tempo.NORMAL).saveGame(g));

        List<Game> games = gameRepository.gameOps(Tempo.NORMAL).recentGames();
        assertThat(games).hasSize(10);
        assertThat(games).extracting("id")
            .containsExactly(
                "id30",
                "id29",
                "id28",
                "id27",
                "id26",
                "id25",
                "id24",
                "id23",
                "id22",
                "id21"
            );
    }

}
