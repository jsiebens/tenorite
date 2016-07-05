package net.tenorite.badges.validators;

import net.tenorite.badges.*;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.GameModeId;
import net.tenorite.game.Player;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class NrOfGamesPlayedTest {

    private static final GameModeId GAME_MODE_ID = GameModeId.of("JUNIT");

    private static final BadgeType BADGE_TYPE = BadgeType.of("junit");

    private static final Badge BADGE = Badge.of(GAME_MODE_ID, BADGE_TYPE);

    private BadgeRepositoryStub badgeRepository = new BadgeRepositoryStub();

    private List<BadgeEarned> published = new ArrayList<>();

    private NrOfGamesPlayed validator;

    @Before
    public void setUp() {
        badgeRepository.clear();
        published.clear();

        validator = new NrOfGamesPlayed(BADGE, 10);
    }

    @Test
    public void testEarnBadge() {
        badgeRepository.updateProgress(BADGE, "john", 9);
        badgeRepository.updateProgress(BADGE, "jane", 9);
        badgeRepository.updateProgress(BADGE, "nick", 7);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, e -> published.add(e));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(published).extracting("upgrade").containsExactly(false, false);
        assertThat(published).extracting("badge.name").containsExactly("john", "jane");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID, GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE, BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(1L, 1L);

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "jane")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "nick")).isEqualTo(8);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of("john", BADGE, 1000, 4, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 9);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, e -> published.add(e));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();

        assertThat(published).extracting("upgrade").containsExactly(true);
        assertThat(published).extracting("badge.name").containsExactly("john");
        assertThat(published).extracting("badge.gameModeId").containsExactly(GAME_MODE_ID);
        assertThat(published).extracting("badge.badgeType").containsExactly(BADGE_TYPE);
        assertThat(published).extracting("badge.level").containsExactly(5L);

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "jane")).isEqualTo(1);
        assertThat(badgeRepository.getProgress(BADGE, "nick")).isEqualTo(1);
    }

}