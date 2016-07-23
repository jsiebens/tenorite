package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class PlineActMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlineActMessage.of(4, "lorem ipsum").raw(Tempo.NORMAL)).isEqualTo("plineact 4 lorem ipsum");
        assertThat(PlineActMessage.of(4, "lorem ipsum").raw(Tempo.FAST)).isEqualTo("plineact 4 lorem ipsum");
    }

}