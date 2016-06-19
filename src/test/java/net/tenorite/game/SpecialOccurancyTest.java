package net.tenorite.game;

import net.tenorite.core.Special;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecialOccurancyTest {

    @Test
    public void testOccurancyShouldBeNormalized() {
        SpecialOccurancy o = SpecialOccurancy.specialOccurancy(s -> s
            .addLine(1)
            .clearLine(2)
            .nukeField(3)
            .randomClear(4)
            .switchField(5)
            .clearSpecial(6)
            .gravity(7)
            .quakeField(8)
            .blockBomb(9));

        assertThat(o.getAddLine()).isEqualTo(o.occurancyOf(Special.ADDLINE)).isEqualTo(3);
        assertThat(o.getClearLine()).isEqualTo(o.occurancyOf(Special.CLEARLINE)).isEqualTo(5);
        assertThat(o.getNukeField()).isEqualTo(o.occurancyOf(Special.NUKEFIELD)).isEqualTo(7);
        assertThat(o.getRandomClear()).isEqualTo(o.occurancyOf(Special.RANDOMCLEAR)).isEqualTo(9);
        assertThat(o.getSwitchField()).isEqualTo(o.occurancyOf(Special.SWITCHFIELD)).isEqualTo(11);
        assertThat(o.getClearSpecial()).isEqualTo(o.occurancyOf(Special.CLEARSPECIAL)).isEqualTo(13);
        assertThat(o.getGravity()).isEqualTo(o.occurancyOf(Special.GRAVITY)).isEqualTo(15);
        assertThat(o.getQuakeField()).isEqualTo(o.occurancyOf(Special.QUAKEFIELD)).isEqualTo(17);
        assertThat(o.getBlockBomb()).isEqualTo(o.occurancyOf(Special.BLOCKBOMB)).isEqualTo(20);
    }

    @Test
    public void testOccurancyShouldBeNormalized2() {
        SpecialOccurancy o = SpecialOccurancy.specialOccurancy(s -> {
        });

        assertThat(o.getAddLine()).isEqualTo(o.occurancyOf(Special.ADDLINE)).isEqualTo(12);
        assertThat(o.getClearLine()).isEqualTo(o.occurancyOf(Special.CLEARLINE)).isEqualTo(11);
        assertThat(o.getNukeField()).isEqualTo(o.occurancyOf(Special.NUKEFIELD)).isEqualTo(11);
        assertThat(o.getRandomClear()).isEqualTo(o.occurancyOf(Special.RANDOMCLEAR)).isEqualTo(11);
        assertThat(o.getSwitchField()).isEqualTo(o.occurancyOf(Special.SWITCHFIELD)).isEqualTo(11);
        assertThat(o.getClearSpecial()).isEqualTo(o.occurancyOf(Special.CLEARSPECIAL)).isEqualTo(11);
        assertThat(o.getGravity()).isEqualTo(o.occurancyOf(Special.GRAVITY)).isEqualTo(11);
        assertThat(o.getQuakeField()).isEqualTo(o.occurancyOf(Special.QUAKEFIELD)).isEqualTo(11);
        assertThat(o.getBlockBomb()).isEqualTo(o.occurancyOf(Special.BLOCKBOMB)).isEqualTo(11);
    }

}
