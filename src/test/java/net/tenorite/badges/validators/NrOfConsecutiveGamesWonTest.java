/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.BadgeRepositoryStub;
import net.tenorite.badges.BadgeType;
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

/**
 * @author Johan Siebens
 */
public class NrOfConsecutiveGamesWonTest {

    private static final GameModeId GAME_MODE_ID = GameModeId.of("JUNIT");

    private static final BadgeType BADGE_TYPE = BadgeType.of("junit");

    private static final Badge BADGE = Badge.of(GAME_MODE_ID, BADGE_TYPE);

    private BadgeRepositoryStub badgeRepository = new BadgeRepositoryStub();

    private List<BadgeEarned> published = new ArrayList<>();

    private NrOfConsecutiveGamesWon validator;

    @Before
    public void setUp() {
        badgeRepository.clear();
        published.clear();

        validator = new NrOfConsecutiveGamesWon(BADGE, 5);
    }

    @Test
    public void testProgressIsIncreasedWhenPlayerWinsAGame() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, e -> published.add(e));

        assertThat(published).isEmpty();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(1);
        assertThat(badgeRepository.getProgress(BADGE, "jane")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "nick")).isEqualTo(0);
    }

    @Test
    public void testEarnBadge() {
        badgeRepository.updateProgress(BADGE, "john", 4);
        badgeRepository.updateProgress(BADGE, "jane", 1);
        badgeRepository.updateProgress(BADGE, "nick", 2);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, e -> published.add(e));

        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 0, 1, "id");

        assertThat(published).containsExactly(BadgeEarned.of(expected, false));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "jane")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "nick")).isEqualTo(0);
    }

    @Test
    public void testUpgradeBadge() {
        badgeRepository.saveBadgeLevel(BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 1000, 4, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 4);
        badgeRepository.updateProgress(BADGE, "jane", 1);
        badgeRepository.updateProgress(BADGE, "nick", 2);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), emptyList());
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, e -> published.add(e));


        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 0, 5, "id");

        assertThat(published).containsExactly(BadgeEarned.of(expected, true));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "jane")).isEqualTo(0);
        assertThat(badgeRepository.getProgress(BADGE, "nick")).isEqualTo(0);
    }

}
