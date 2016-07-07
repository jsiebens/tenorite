package net.tenorite.badges.validators;

import net.tenorite.badges.BadgeLevel;
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

public class NrOfSpecialsUsedTest extends AbstractValidatorTestCase {

    private NrOfSpecialsUsed validator = new NrOfSpecialsUsed(BADGE, s -> true);

    @Test
    public void testEarnBadge() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 5)
            .putNrOfSpecialsOnTeamPlayer(Special.CLEARLINE, 9)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).extracting("upgrade").containsExactly(false);
        assertThat(published).extracting("badge.name").containsExactly("john");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(14L);

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(14);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of("john", BADGE, 1000, 11, "gameId"));

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null), b -> b
            .putNrOfSpecialsOnOpponent(Special.RANDOMCLEAR, 5)
            .putNrOfSpecialsOnTeamPlayer(Special.CLEARLINE, 9)
        );
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        assertThat(published).extracting("upgrade").containsExactly(true);
        assertThat(published).extracting("badge.name").containsExactly("john");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(14L);

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(14);
    }

}