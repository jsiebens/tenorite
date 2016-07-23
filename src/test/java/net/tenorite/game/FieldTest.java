package net.tenorite.game;

import org.junit.Test;

import static net.tenorite.core.Special.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class FieldTest {

    @Test
    public void testFullUpdate() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000005500");
        buffer.append("000000005500");
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

        Field field = Field.empty().update(buffer.toString());

        assertThat(field.getBlock(8, 16)).isEqualTo('5');
        assertThat(field.getBlock(8, 17)).isEqualTo('5');
        assertThat(field.getBlock(9, 16)).isEqualTo('5');
        assertThat(field.getBlock(9, 17)).isEqualTo('5');
    }

    @Test
    public void testPartialUpdate1() {
        Field field = Field.empty().update("$3G3H4H5H");

        assertThat(field.getBlock(0, 0)).isEqualTo('3');
        assertThat(field.getBlock(1, 0)).isEqualTo('3');
        assertThat(field.getBlock(2, 0)).isEqualTo('3');
        assertThat(field.getBlock(0, 1)).isEqualTo('3');
    }

    @Test
    public void testPartialUpdate2() {
        Field field = Field.empty().update("$3G3H4H5H\";H<H=H>H");

        assertThat(field.getBlock(0, 0)).isEqualTo('3');
        assertThat(field.getBlock(1, 0)).isEqualTo('3');
        assertThat(field.getBlock(2, 0)).isEqualTo('3');
        assertThat(field.getBlock(0, 1)).isEqualTo('3');

        assertThat(field.getBlock(8, 0)).isEqualTo('1');
        assertThat(field.getBlock(9, 0)).isEqualTo('1');
        assertThat(field.getBlock(10, 0)).isEqualTo('1');
        assertThat(field.getBlock(11, 0)).isEqualTo('1');
    }

    @Test
    public void testPartialUpdate3() {
        Field field = Field.empty().update("+3H-4H.5H");

        assertThat(field.getBlock(0, 0)).isEqualTo(SWITCHFIELD.getLetter());
        assertThat(field.getBlock(1, 0)).isEqualTo(GRAVITY.getLetter());
        assertThat(field.getBlock(2, 0)).isEqualTo(QUAKEFIELD.getLetter());
    }

    @Test
    public void testGetFieldString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("100000000001");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000005500");
        buffer.append("000000005500");
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
        buffer.append("acnrsbgqo000");
        buffer.append("012345000001");

        Field field = Field.empty().update(buffer.toString());

        assertThat(field.getFieldString())
            .isNotNull()
            .isEqualTo(buffer.toString());
    }

    @Test
    public void testGetHighest() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000005500");
        buffer.append("000000005500");
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
        buffer.append("acnrsbgqo000");
        buffer.append("012345000001");

        assertThat(Field.empty().getHighest()).isEqualTo(0);
        assertThat(Field.of(buffer.toString()).getHighest()).isEqualTo(18);
    }

}
