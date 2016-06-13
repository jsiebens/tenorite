package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class StartGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(StartGameMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("startgame 1 4");
        assertThat(StartGameMessage.of(4).raw(Tempo.FAST)).isEqualTo("startgame 1 4");
    }

}