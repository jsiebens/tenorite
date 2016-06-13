package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ClassicStyleAddMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(ClassicStyleAddMessage.of(1, 4).raw(Tempo.NORMAL)).isEqualTo("sb 0 cs4 1");
        assertThat(ClassicStyleAddMessage.of(1, 4).raw(Tempo.FAST)).isEqualTo("sb 0 cs4 1");
    }

}