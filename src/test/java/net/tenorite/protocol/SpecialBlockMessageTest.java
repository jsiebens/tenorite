package net.tenorite.protocol;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class SpecialBlockMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(SpecialBlockMessage.of(4, Special.ADDLINE, 3).raw(Tempo.NORMAL)).isEqualTo("sb 3 a 4");
        assertThat(SpecialBlockMessage.of(4, Special.ADDLINE, 3).raw(Tempo.FAST)).isEqualTo("sb 3 a 4");
    }

}