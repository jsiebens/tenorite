package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PlayerNumMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerNumMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("playernum 4");
        assertThat(PlayerNumMessage.of(4).raw(Tempo.FAST)).isEqualTo(")#)(!@(*3 4");
    }

}