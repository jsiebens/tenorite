package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GamePausedMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(GamePausedMessage.of().raw(Tempo.NORMAL)).isEqualTo("pause 1");
        assertThat(GamePausedMessage.of().raw(Tempo.FAST)).isEqualTo("pause 1");
    }

}