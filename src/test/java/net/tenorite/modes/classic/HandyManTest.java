package net.tenorite.modes.classic;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.badges.validators.AbstractValidatorTestCase;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class HandyManTest extends AbstractValidatorTestCase {

    private HandyMan validator = new HandyMan(BADGE);

    @Test
    public void testIgnoreWhenSomeSpecialsAreNotUsed() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .putNrOfSpecialsOnOpponent(Special.ADDLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.CLEARLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.SWITCHFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.GRAVITY, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.QUAKEFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.BLOCKBOMB, 1)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).isEmpty();
        assertThat(badgeRepository.getBadgeLevel("john", BADGE)).isEmpty();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE)).isEmpty();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE)).isEmpty();
    }

    @Test
    public void testEarnBadge() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .putNrOfSpecialsOnOpponent(Special.ADDLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.CLEARLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.NUKEFIELD, 1)
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.SWITCHFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.CLEARSPECIAL, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.GRAVITY, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.QUAKEFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.BLOCKBOMB, 1)
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

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(1);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 1000, 13, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 13);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .putNrOfSpecialsOnOpponent(Special.ADDLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.CLEARLINE, 1)
            .putNrOfSpecialsOnOpponent(Special.NUKEFIELD, 1)
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.SWITCHFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.CLEARSPECIAL, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.GRAVITY, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.QUAKEFIELD, 1)
            .putNrOfSpecialsOnTeamPlayer(Special.BLOCKBOMB, 1)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 0, 14, "id");

        assertThat(published).containsExactly(BadgeEarned.of(expected, true));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(14);
    }

}
