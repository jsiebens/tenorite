package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class PlayerWonMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerWonMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("playerwon 4");
        assertThat(PlayerWonMessage.of(4).raw(Tempo.FAST)).isEqualTo("playerwon 4");
    }

}