package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PlayerJoinMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerJoinMessage.of(4, "john").raw(Tempo.NORMAL)).isEqualTo("playerjoin 4 john");
        assertThat(PlayerJoinMessage.of(4, "jane").raw(Tempo.FAST)).isEqualTo("playerjoin 4 jane");
    }

}