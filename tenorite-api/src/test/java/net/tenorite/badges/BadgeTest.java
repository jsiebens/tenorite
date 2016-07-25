package net.tenorite.badges;

import net.tenorite.game.GameModeId;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class BadgeTest {

    @Test
    public void testTitleAndDescription() {
        Badge badge = Badge.of(GameModeId.of("TEST"), "BADGE_A");

        assertThat(badge.getTitle()).isEqualTo("Title of Badge A");
        assertThat(badge.getDescription()).isEqualTo("Description of Badge A");
    }

    @Test
    public void testTitleAndDescriptionWhenBadgesPropertiesFileIsMissing() {
        Badge badge = Badge.of(GameModeId.of("UNKNOWN"), "BADGE_A");

        assertThat(badge.getTitle()).isEqualTo("BADGE_A.title");
        assertThat(badge.getDescription()).isEqualTo("BADGE_A.description");
    }

}
