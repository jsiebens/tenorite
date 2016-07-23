package net.tenorite.badges.validators;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
@RunWith(Parameterized.class)
public class NrOfConsecutiveCombosTest extends AbstractValidatorTestCase {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][]{
            {new NrOfConsecutiveCombos(BADGE, 17, 5)},
            {new NrOfConsecutiveTwoLineCombos(BADGE, 7, 5)},
            {new NrOfConsecutiveThreeLineCombos(BADGE, 6, 5)},
            {new NrOfConsecutiveFourLineCombos(BADGE, 5, 5)}
        });
    }

    private BadgeValidator validator;

    public NrOfConsecutiveCombosTest(BadgeValidator validator) {
        this.validator = validator;
    }

    @Test
    public void testProgressIsIncreasedWhenRequiredNrOfCombosIsReached() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .nrOfTwoLineCombos(7)
            .nrOfThreeLineCombos(6)
            .nrOfFourLineCombos(5)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).isEmpty();

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(1);
    }

    @Test
    public void testEarnBadge() {
        badgeRepository.updateProgress(BADGE, "john", 4);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .nrOfTwoLineCombos(7)
            .nrOfThreeLineCombos(6)
            .nrOfFourLineCombos(5)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 0, 1, "id");

        assertThat(published).containsExactly(BadgeEarned.of(expected, false));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 1000, 3, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 4);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .nrOfTwoLineCombos(7)
            .nrOfThreeLineCombos(6)
            .nrOfFourLineCombos(5)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("newId", 2000, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 2000, 4, "newId");

        assertThat(published).containsExactly(BadgeEarned.of(expected, true));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE)).hasValue(BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 2000, 4, "newId"));
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE)).isEmpty();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE)).isEmpty();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
    }

}
