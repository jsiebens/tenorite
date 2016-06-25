package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.PlayerLeaveMessage;
import net.tenorite.protocol.PlayerLostMessage;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class GameRankingCalculatorTest {

    private static Game newGame(GameMode gameMode, Consumer<GameBuilder> consumer) {
        GameBuilder builder = new GameBuilder().from(Game.of("id", 0, 2000, Tempo.NORMAL, gameMode, emptyList(), emptyList()));
        consumer.accept(builder);
        return builder.build();
    }

    private GameRankCalculator calculator = new GameRankCalculator();

    @Test
    public void testResultShouldBeEmptyWhenEverybodyLeavesGameBeforeEnd() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(GameMode.CLASSIC, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, PlayerLeaveMessage.of(1)),
                GameMessage.of(100, PlayerLeaveMessage.of(2))
            )
        );

        assertThat(calculator.calculate(game)).isEmpty();
    }

    @Test
    public void testResultShouldBeOrderedByWinnerFirst() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", null);

        Game game = newGame(GameMode.CLASSIC, b -> b
            .addPlayers(playerA, playerB, playerC)
            .addMessages(
                GameMessage.of(100, PlayerLostMessage.of(2)),
                GameMessage.of(200, PlayerLostMessage.of(1))
            )
        );

        List<PlayingStats> result = calculator.calculate(game);

        assertThat(result).extracting("player.name").containsExactly("C", "A", "B");
    }

    @Test
    public void testCalculatorShouldTrackPlayingTimeOfFinishingPlayers() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", "doe");
        Player playerD = Player.of(4, "D", "doe");

        Game game = newGame(GameMode.CLASSIC, b -> b
            .addPlayers(playerA, playerB, playerC, playerD)
            .addMessages(
                GameMessage.of(100, PlayerLeaveMessage.of(2)),
                GameMessage.of(200, PlayerLostMessage.of(1))
            )
        );

        List<PlayingStats> result = calculator.calculate(game);

        assertThat(result).extracting("playingTime").containsExactly(2000L, 2000L, 200L);
    }

    @Test
    public void testCalculatorShouldRecordLevelForEachPlayer() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(GameMode.CLASSIC, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, LvlMessage.of(1, 5)),
                GameMessage.of(200, LvlMessage.of(2, 5)),
                GameMessage.of(300, LvlMessage.of(1, 16)),
                GameMessage.of(400, LvlMessage.of(2, 17)),
                GameMessage.of(500, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(game);

        assertThat(result).extracting("level").containsExactly(16, 17);
    }

}
