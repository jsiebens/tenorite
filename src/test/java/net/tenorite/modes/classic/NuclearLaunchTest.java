package net.tenorite.modes.classic;

import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.badges.validators.AbstractValidatorTestCase;
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
public class NuclearLaunchTest extends AbstractValidatorTestCase {

    private NuclearLaunch validator = new NuclearLaunch(BADGE);

    @Test
    public void testEarnBadge() {
        PlayingStats player1 = PlayingStats.of(Player.of(1, "john", null));
        PlayingStats player2 = PlayingStats.of(Player.of(2, "jane", null));
        PlayingStats player3 = PlayingStats.of(Player.of(3, "nick", null));

        List<GameMessage> messages = Arrays.asList(
            GameMessage.of(1000, FieldMessage.of(2, Field.randomCompletedField().getFieldString())),
            GameMessage.of(2000, FieldMessage.of(2, createFieldWith3Bombs())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.BLOCKBOMB, 2))
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
            GameMessage.of(2000, FieldMessage.of(2, createFieldWith3Bombs())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.BLOCKBOMB, 2)),
            GameMessage.of(1000, FieldMessage.of(2, Field.randomCompletedField().getFieldString())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.BLOCKBOMB, 2)),
            GameMessage.of(2000, FieldMessage.of(2, createFieldWith3Bombs())),
            GameMessage.of(3000, SpecialBlockMessage.of(1, Special.BLOCKBOMB, 2))
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

    private String createFieldWith3Bombs() {
        return emptyField() + "00000000ooo0";
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
        buffer.append("000000000000");
        return buffer.toString();
    }

}
