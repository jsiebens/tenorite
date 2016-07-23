package net.tenorite.net;

import net.tenorite.AbstractTestCase;
import org.junit.Test;

import java.util.Optional;

import static net.tenorite.net.InitTokenDecoder.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class InitTokenDecoderTest extends AbstractTestCase {

    @Test
    public void testEncodeDecodeTetrinet() {
        String init = encode(TETRISSTART, "junit", "1.13");
        Optional<String> decode = decode(init);
        assertThat(decode).hasValue("tetrisstart junit 1.13");
    }

    @Test
    public void testEncodeDecodeTetrifast() {
        String init = encode(TETRIFASTER, "junit", "1.13");
        Optional<String> decode = decode(init);
        assertThat(decode).hasValue("tetrifaster junit 1.13");
    }

    @Test
    public void testEncodeInvalid() {
        assertThat(decode("helloworld").isPresent()).isFalse();
        assertThat(decode(encode("unknown", "junit", "1.13")).isPresent()).isFalse();
    }

}
