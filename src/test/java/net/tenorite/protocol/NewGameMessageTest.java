package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class NewGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(NewGameMessage.of("loremipsum").raw(Tempo.NORMAL)).isEqualTo("newgame loremipsum");
        assertThat(NewGameMessage.of("loremipsum").raw(Tempo.FAST)).isEqualTo("******* loremipsum");
    }

}