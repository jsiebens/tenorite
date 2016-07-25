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
package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class PlayerJoinMessageTest {

    @Test
    public void raw() throws Exception {
        assertThat(PlayerJoinMessage.of(4, "john").raw(Tempo.NORMAL)).isEqualTo("playerjoin 4 john");
        assertThat(PlayerJoinMessage.of(4, "jane").raw(Tempo.FAST)).isEqualTo("playerjoin 4 jane");
    }

}