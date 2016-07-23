package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class PauseGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PauseGameMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("pause 1 4");
        assertThat(PauseGameMessage.of(4).raw(Tempo.FAST)).isEqualTo("pause 1 4");
    }

}