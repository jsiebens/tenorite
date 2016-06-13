package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PlayerLostMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerLostMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("playerlost 4");
        assertThat(PlayerLostMessage.of(4).raw(Tempo.FAST)).isEqualTo("playerlost 4");
    }

}