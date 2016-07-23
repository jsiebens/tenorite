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
package net.tenorite.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.tenorite.core.Special;
import net.tenorite.protocol.SpecialBlockMessage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameMessageTest {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Test
    public void testJson() throws Exception {
        GameMessage expected = GameMessage.of(1000, SpecialBlockMessage.of(1, Special.ADDLINE, 2));
        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, GameMessage.class)).isEqualTo(expected);
    }

}
