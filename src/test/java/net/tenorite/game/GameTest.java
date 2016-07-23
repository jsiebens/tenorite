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
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.modes.classic.Classic;
import net.tenorite.protocol.ClassicStyleAddMessage;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.SpecialBlockMessage;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameTest {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new GuavaModule());

    @Test
    public void testJson() throws Exception {
        Game expected =
            Game.of("game1", 1000, 250, Tempo.NORMAL, Classic.ID,
                asList(
                    Player.of(1, "john", "doe"),
                    Player.of(2, "jane", "doe"),
                    Player.of(3, "nick", null)
                ),
                asList(
                    GameMessage.of(50, SpecialBlockMessage.of(1, Special.ADDLINE, 3)),
                    GameMessage.of(75, ClassicStyleAddMessage.of(2, 4)),
                    GameMessage.of(100, LvlMessage.of(2, 4))
                )
            );

        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, Game.class)).isEqualTo(expected);
    }

}
