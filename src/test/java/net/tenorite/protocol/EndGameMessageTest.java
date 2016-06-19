package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EndGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(EndGameMessage.of().raw(Tempo.NORMAL)).isEqualTo("endgame");
        assertThat(EndGameMessage.of().raw(Tempo.FAST)).isEqualTo("endgame");
    }

}