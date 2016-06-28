package net.tenorite.game;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.modes.Classic;
import net.tenorite.game.modes.Default;
import net.tenorite.protocol.*;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class GameRankingCalculatorTest {

    private static Game newGame(GameModeId gameModeId, Consumer<GameBuilder> consumer) {
        GameBuilder builder = new GameBuilder().from(Game.of("id", 0, 2000, Tempo.NORMAL, gameModeId, emptyList(), emptyList()));
        consumer.accept(builder);
        return builder.build();
    }

    private GameRankCalculator calculator = new GameRankCalculator();

    @Test
    public void testResultShouldBeEmptyWhenEverybodyLeavesGameBeforeEnd() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, PlayerLeaveMessage.of(1)),
                GameMessage.of(100, PlayerLeaveMessage.of(2))
            )
        );

        assertThat(calculator.calculate(new Classic(), game)).isEmpty();
    }

    @Test
    public void testResultShouldBeOrderedByWinnerFirst() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB, playerC)
            .addMessages(
                GameMessage.of(100, PlayerLostMessage.of(2)),
                GameMessage.of(200, PlayerLostMessage.of(1))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("player.name").containsExactly("C", "A", "B");
    }

    @Test
    public void testCalculatorShouldTrackPlayingTimeOfFinishingPlayers() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", "doe");
        Player playerD = Player.of(4, "D", "doe");

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB, playerC, playerD)
            .addMessages(
                GameMessage.of(100, PlayerLeaveMessage.of(2)),
                GameMessage.of(200, PlayerLostMessage.of(1))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("playingTime").containsExactly(2000L, 2000L, 200L);
    }

    @Test
    public void testCalculatorShouldRecordLevelForEachPlayer() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, LvlMessage.of(1, 5)),
                GameMessage.of(200, LvlMessage.of(2, 5)),
                GameMessage.of(300, LvlMessage.of(1, 16)),
                GameMessage.of(400, LvlMessage.of(2, 17)),
                GameMessage.of(500, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("level").containsExactly(16, 17);
    }

    @Test
    public void testCalculatorShouldRecordNrOfLinesBasedOnLevelForEachPlayer() {
        Classic gameMode = new Classic();

        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, LvlMessage.of(1, 5)),
                GameMessage.of(200, LvlMessage.of(2, 5)),
                GameMessage.of(300, LvlMessage.of(1, 16)),
                GameMessage.of(400, LvlMessage.of(2, 17)),
                GameMessage.of(500, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(gameMode, game);

        int lpl = gameMode.gameRules().getLinesPerLevel();

        assertThat(result).extracting("nrOfLines").containsExactly(16 * lpl, 17 * lpl);
    }

    @Test
    public void testCalcularShouldUseClassicSpecialsToTrackNrOfCombos() {

        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, ClassicStyleAddMessage.of(1, 1)),
                GameMessage.of(200, ClassicStyleAddMessage.of(1, 2)),
                GameMessage.of(300, ClassicStyleAddMessage.of(1, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(1, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(0, 1)),
                GameMessage.of(600, ClassicStyleAddMessage.of(1, 2)),
                GameMessage.of(700, ClassicStyleAddMessage.of(1, 4)),
                GameMessage.of(900, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfTwoLineCombos").containsExactly(3, 0);
        assertThat(result).extracting("nrOfThreeLineCombos").containsExactly(2, 0);
        assertThat(result).extracting("nrOfFourLineCombos").containsExactly(1, 0);
    }

    @Test
    public void testCalculatorShouldTrackMaxFieldHeightOfFinishingPlayers() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, createField(10))),
                GameMessage.of(200, FieldMessage.of(1, createField(5))),
                GameMessage.of(300, FieldMessage.of(2, createField(22))),
                GameMessage.of(400, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("maxFieldHeight").containsExactly(10, 22);
    }

    @Test
    public void testCalculatorShouldRecordBlockCountsWhenFieldIsUpdatedExceptFirstUpdate() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "john", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(2, 0);
    }

    @Test
    public void testCalculatorShouldDecreaseBlockCountOfTargetWhenSpecialIsSent() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "john", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, SpecialBlockMessage.of(2, Special.ADDLINE, 1)),
                GameMessage.of(600, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(1, 0);
    }

    @Test
    public void testCalculatorShouldDecreaseBlockCountOfAllOpponentsWhenPlayerSentAClassicSpecial() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "john", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 2)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 4)),
                GameMessage.of(600, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(1, 0);
    }

    @Test
    public void testCalculatorShouldNotDecreaseBlockCountWhenClassicSpecialIsSentAndClassicModeIsDisabled() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "john", null);

        Game game = newGame(Default.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 2)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 4)),
                GameMessage.of(600, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Default(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(4, 0);
    }

    @Test
    public void testGameShouldDecreaseBlockCountWhenClassicSpecialIsSentByServerEvenWhenClassicModeIsDisabled() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "john", null);

        Game game = newGame(Default.ID, b -> b
            .addPlayers(playerA, playerB)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, ClassicStyleAddMessage.of(0, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(0, 2)),
                GameMessage.of(500, ClassicStyleAddMessage.of(0, 4)),
                GameMessage.of(600, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(1, 0);
    }

    @Test
    public void testCalculatorShouldNotDecreaseBlockCountOfTeamPlayersWhenPlayerSentAClassicSpecial() {
        Player playerA = Player.of(1, "nick", "doe");
        Player playerB = Player.of(2, "john", "doe");
        Player playerC = Player.of(3, "jane", null);

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB, playerC)
            .addMessages(
                GameMessage.of(100, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(500, FieldMessage.of(1, "$3G3H4H5H")),
                GameMessage.of(100, FieldMessage.of(3, "$3G3H4H5H")),
                GameMessage.of(200, FieldMessage.of(3, "$3G3H4H5H")),
                GameMessage.of(300, FieldMessage.of(3, "$3G3H4H5H")),
                GameMessage.of(400, FieldMessage.of(3, "$3G3H4H5H")),
                GameMessage.of(500, FieldMessage.of(3, "$3G3H4H5H")),
                GameMessage.of(600, FieldMessage.of(2, "$3G3H4H5H")),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 1)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 2)),
                GameMessage.of(500, ClassicStyleAddMessage.of(2, 4)),
                GameMessage.of(1200, PlayerLostMessage.of(2)),
                GameMessage.of(1300, PlayerLostMessage.of(3))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result).extracting("nrOfBlocks").containsExactly(4, 1, 0);
    }

    @Test
    public void testCalculatorShouldTrackNrOfSpecials() {
        Player playerA = Player.of(1, "john", "doe");
        Player playerB = Player.of(2, "nick", "");
        Player playerC = Player.of(3, "jane", "doe");

        Game game = newGame(Classic.ID, b -> b
            .addPlayers(playerA, playerB, playerC)
            .addMessages(
                GameMessage.of(100, SpecialBlockMessage.of(1, Special.ADDLINE, 2)),
                GameMessage.of(200, SpecialBlockMessage.of(1, Special.ADDLINE, 2)),
                GameMessage.of(300, SpecialBlockMessage.of(1, Special.ADDLINE, 2)),
                GameMessage.of(400, SpecialBlockMessage.of(1, Special.CLEARLINE, 2)),
                GameMessage.of(400, SpecialBlockMessage.of(1, Special.GRAVITY, 1)),
                GameMessage.of(500, SpecialBlockMessage.of(1, Special.CLEARLINE, 2)),
                GameMessage.of(600, SpecialBlockMessage.of(1, Special.QUAKEFIELD, 2)),
                GameMessage.of(600, SpecialBlockMessage.of(1, Special.NUKEFIELD, 1)),
                GameMessage.of(600, SpecialBlockMessage.of(1, Special.CLEARLINE, 3)),
                GameMessage.of(600, SpecialBlockMessage.of(1, Special.CLEARLINE, 3)),
                GameMessage.of(600, SpecialBlockMessage.of(1, Special.GRAVITY, 3)),
                GameMessage.of(600, PlayerLostMessage.of(3)),
                GameMessage.of(600, PlayerLostMessage.of(2))
            )
        );

        List<PlayingStats> result = calculator.calculate(new Classic(), game);

        assertThat(result.get(0).getNrOfSpecialsOnOpponent()).containsOnly(
            entry(Special.ADDLINE, 3),
            entry(Special.CLEARLINE, 2),
            entry(Special.NUKEFIELD, 0),
            entry(Special.RANDOMCLEAR, 0),
            entry(Special.SWITCHFIELD, 0),
            entry(Special.CLEARSPECIAL, 0),
            entry(Special.GRAVITY, 0),
            entry(Special.QUAKEFIELD, 1),
            entry(Special.BLOCKBOMB, 0)
        );

        assertThat(result.get(0).getNrOfSpecialsOnTeamPlayer()).containsOnly(
            entry(Special.ADDLINE, 0),
            entry(Special.CLEARLINE, 2),
            entry(Special.NUKEFIELD, 0),
            entry(Special.RANDOMCLEAR, 0),
            entry(Special.SWITCHFIELD, 0),
            entry(Special.CLEARSPECIAL, 0),
            entry(Special.GRAVITY, 1),
            entry(Special.QUAKEFIELD, 0),
            entry(Special.BLOCKBOMB, 0)
        );

        assertThat(result.get(0).getNrOfSpecialsOnSelf()).containsOnly(
            entry(Special.ADDLINE, 0),
            entry(Special.CLEARLINE, 0),
            entry(Special.NUKEFIELD, 1),
            entry(Special.RANDOMCLEAR, 0),
            entry(Special.SWITCHFIELD, 0),
            entry(Special.CLEARSPECIAL, 0),
            entry(Special.GRAVITY, 1),
            entry(Special.QUAKEFIELD, 0),
            entry(Special.BLOCKBOMB, 0)
        );

        assertThat(result.get(1).getNrOfSpecialsReceived()).containsOnly(
            entry(Special.ADDLINE, 3),
            entry(Special.CLEARLINE, 2),
            entry(Special.NUKEFIELD, 0),
            entry(Special.RANDOMCLEAR, 0),
            entry(Special.SWITCHFIELD, 0),
            entry(Special.CLEARSPECIAL, 0),
            entry(Special.GRAVITY, 0),
            entry(Special.QUAKEFIELD, 1),
            entry(Special.BLOCKBOMB, 0)
        );
    }

    private String createField(int maxHeight) {
        StringBuilder builder = new StringBuilder();
        for (int i = Field.HEIGHT - 1; i >= 0; i--) {
            if (i == (maxHeight - 1)) {
                builder.append("000000550000");
            }
            else {
                builder.append("000000000000");
            }
        }
        return builder.toString();
    }

}
