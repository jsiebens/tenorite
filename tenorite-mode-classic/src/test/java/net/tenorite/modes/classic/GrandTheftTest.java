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
package net.tenorite.modes.classic;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.events.GameFinished;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.SpecialBlockMessage;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GrandTheftTest extends AbstractValidatorTestCase {

    private GrandTheft validator = new GrandTheft(BADGE);

    @Test
    public void testEarnBadge() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        List<GameMessage> messages = Arrays.asList(
            GameMessage.of(1000, FieldMessage.of(2, Field.randomCompletedField().getFieldString())),
            GameMessage.of(2000, FieldMessage.of(2, createFieldWithMoreThen20Specials())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.CLEARSPECIAL, 2))
        );

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), messages);
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
        badgeRepository.saveBadgeLevel(BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 1000, 3, "gameId"));
        badgeRepository.updateProgress(BADGE, "john", 3);

        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        List<GameMessage> messages = Arrays.asList(
            GameMessage.of(1000, FieldMessage.of(2, Field.randomCompletedField().getFieldString())),
            GameMessage.of(2000, FieldMessage.of(2, createFieldWithMoreThen20Specials())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.CLEARSPECIAL, 2)),
            GameMessage.of(1000, FieldMessage.of(2, Field.randomCompletedField().getFieldString())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.CLEARSPECIAL, 2)),
            GameMessage.of(2000, FieldMessage.of(2, createFieldWithMoreThen20Specials())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.CLEARSPECIAL, 2))
        );

        Game game = Game.of("id", 0, 100, Tempo.NORMAL, GAME_MODE_ID, emptyList(), messages);
        GameFinished gameFinished = GameFinished.of(game, asList(player1, player2, player3));

        validator.process(gameFinished, badgeRepository, published::add);

        BadgeLevel expected = BadgeLevel.of(Tempo.NORMAL, BADGE, "john", 0, 5, "id");

        assertThat(published).containsExactly(BadgeEarned.of(expected, true));

        assertThat(badgeRepository.getBadgeLevel("john", BADGE).isPresent()).isTrue();
        assertThat(badgeRepository.getBadgeLevel("jane", BADGE).isPresent()).isFalse();
        assertThat(badgeRepository.getBadgeLevel("nick", BADGE).isPresent()).isFalse();

        assertThat(badgeRepository.getProgress(BADGE, "john")).isEqualTo(5);
    }

    private String createFieldWithMoreThen20Specials() {
        return emptyField() + "acnboqagaccb" + "ooacoonggbbc";
    }

    private String emptyField() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        return buffer.toString();
    }

}
