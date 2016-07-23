package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class PlineMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlineMessage.of(4, "lorem ipsum").raw(Tempo.NORMAL)).isEqualTo("pline 4 lorem ipsum");
        assertThat(PlineMessage.of(4, "lorem ipsum").raw(Tempo.FAST)).isEqualTo("pline 4 lorem ipsum");
    }

}