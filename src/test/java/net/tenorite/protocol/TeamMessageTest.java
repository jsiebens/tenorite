package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class TeamMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(TeamMessage.of(4, "lorem ipsum").raw(Tempo.NORMAL)).isEqualTo("team 4 lorem ipsum");
        assertThat(TeamMessage.of(4, "lorem ipsum").raw(Tempo.FAST)).isEqualTo("team 4 lorem ipsum");
    }

}