package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FieldMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(FieldMessage.of(1, "thisisthefieldupdate").raw(Tempo.NORMAL)).isEqualTo("f 1 thisisthefieldupdate");
        assertThat(FieldMessage.of(1, "thisisthefieldupdate").raw(Tempo.FAST)).isEqualTo("f 1 thisisthefieldupdate");
    }

}