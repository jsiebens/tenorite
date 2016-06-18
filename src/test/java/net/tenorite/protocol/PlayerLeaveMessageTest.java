package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PlayerLeaveMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerLeaveMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("playerleave 4");
        assertThat(PlayerLeaveMessage.of(4).raw(Tempo.FAST)).isEqualTo("playerleave 4");
    }

}