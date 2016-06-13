package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class StopGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(StopGameMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("startgame 0 4");
        assertThat(StopGameMessage.of(4).raw(Tempo.FAST)).isEqualTo("startgame 0 4");
    }

}