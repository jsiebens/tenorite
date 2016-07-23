package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameRunningMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(GameRunningMessage.of().raw(Tempo.NORMAL)).isEqualTo("pause 0");
        assertThat(GameRunningMessage.of().raw(Tempo.FAST)).isEqualTo("pause 0");
    }

}