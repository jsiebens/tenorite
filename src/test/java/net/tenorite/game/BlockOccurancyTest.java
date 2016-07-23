package net.tenorite.game;

import net.tenorite.core.Block;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class BlockOccurancyTest {

    @Test
    public void testOccurancyShouldBeNormalized() {
        BlockOccurancy o = BlockOccurancy.blockOccurancy(b -> b
            .line(1)
            .square(2)
            .rightL(3)
            .rightZ(4)
            .leftL(5)
            .leftZ(6)
            .halfCross(7));

        assertThat(o.getLine()).isEqualTo(o.occurancyOf(Block.LINE)).isEqualTo(4);
        assertThat(o.getSquare()).isEqualTo(o.occurancyOf(Block.SQUARE)).isEqualTo(8);
        assertThat(o.getRightL()).isEqualTo(o.occurancyOf(Block.RIGHTL)).isEqualTo(10);
        assertThat(o.getRightZ()).isEqualTo(o.occurancyOf(Block.RIGHTZ)).isEqualTo(14);
        assertThat(o.getLeftL()).isEqualTo(o.occurancyOf(Block.LEFTL)).isEqualTo(18);
        assertThat(o.getLeftZ()).isEqualTo(o.occurancyOf(Block.LEFTZ)).isEqualTo(21);
        assertThat(o.getHalfCross()).isEqualTo(o.occurancyOf(Block.HALFCROSS)).isEqualTo(25);
    }

    @Test
    public void testOccurancyShouldBeNormalized2() {
        BlockOccurancy o = BlockOccurancy.blockOccurancy(b -> b.square(25).line(25));

        assertThat(o.getLine()).isEqualTo(50);
        assertThat(o.getSquare()).isEqualTo(50);
    }

    @Test
    public void testOccurancyShouldBeNormalized3() {
        BlockOccurancy o = BlockOccurancy.blockOccurancy(b -> b.square(25).line(25).halfCross(0));

        assertThat(o.getLine()).isEqualTo(50);
        assertThat(o.getSquare()).isEqualTo(50);
    }

    @Test
    public void testOccurancyShouldBeNormalized4() {
        BlockOccurancy o = BlockOccurancy.blockOccurancy(b -> {
        });

        assertThat(o.getLine()).isEqualTo(o.occurancyOf(Block.LINE)).isEqualTo(15);
        assertThat(o.getSquare()).isEqualTo(o.occurancyOf(Block.SQUARE)).isEqualTo(15);
        assertThat(o.getRightL()).isEqualTo(o.occurancyOf(Block.RIGHTL)).isEqualTo(14);
        assertThat(o.getRightZ()).isEqualTo(o.occurancyOf(Block.RIGHTZ)).isEqualTo(14);
        assertThat(o.getLeftL()).isEqualTo(o.occurancyOf(Block.LEFTL)).isEqualTo(14);
        assertThat(o.getLeftZ()).isEqualTo(o.occurancyOf(Block.LEFTZ)).isEqualTo(14);
        assertThat(o.getHalfCross()).isEqualTo(o.occurancyOf(Block.HALFCROSS)).isEqualTo(14);
    }

}
