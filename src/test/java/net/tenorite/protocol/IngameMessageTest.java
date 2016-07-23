package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class IngameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(IngameMessage.of().raw(Tempo.NORMAL)).isEqualTo("ingame");
        assertThat(IngameMessage.of().raw(Tempo.FAST)).isEqualTo("ingame");
    }

}