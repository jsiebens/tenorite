package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class LvlMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(LvlMessage.of(1, 12).raw(Tempo.NORMAL)).isEqualTo("lvl 1 12");
        assertThat(LvlMessage.of(1, 12).raw(Tempo.FAST)).isEqualTo("lvl 1 12");
    }

}