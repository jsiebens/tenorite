package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GmsgMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(GmsgMessage.of("lorem ipsum").raw(Tempo.NORMAL)).isEqualTo("gmsg lorem ipsum");
        assertThat(GmsgMessage.of("lorem ipsum").raw(Tempo.FAST)).isEqualTo("gmsg lorem ipsum");
    }

}