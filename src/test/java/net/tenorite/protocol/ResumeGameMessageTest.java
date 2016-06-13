package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ResumeGameMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(ResumeGameMessage.of(4).raw(Tempo.NORMAL)).isEqualTo("pause 0 4");
        assertThat(ResumeGameMessage.of(4).raw(Tempo.FAST)).isEqualTo("pause 0 4");
    }

}