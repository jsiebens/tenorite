/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
