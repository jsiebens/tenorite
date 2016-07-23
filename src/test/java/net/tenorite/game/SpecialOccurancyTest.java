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

import net.tenorite.core.Special;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class SpecialOccurancyTest {

    @Test
    public void testOccurancyShouldBeNormalized() {
        SpecialOccurancy o = SpecialOccurancy.specialOccurancy(s -> s
            .addLine(1)
            .clearLine(2)
            .nukeField(3)
            .randomClear(4)
            .switchField(5)
            .clearSpecial(6)
            .gravity(7)
            .quakeField(8)
            .blockBomb(9));

        assertThat(o.getAddLine()).isEqualTo(o.occurancyOf(Special.ADDLINE)).isEqualTo(3);
        assertThat(o.getClearLine()).isEqualTo(o.occurancyOf(Special.CLEARLINE)).isEqualTo(5);
        assertThat(o.getNukeField()).isEqualTo(o.occurancyOf(Special.NUKEFIELD)).isEqualTo(7);
        assertThat(o.getRandomClear()).isEqualTo(o.occurancyOf(Special.RANDOMCLEAR)).isEqualTo(9);
        assertThat(o.getSwitchField()).isEqualTo(o.occurancyOf(Special.SWITCHFIELD)).isEqualTo(11);
        assertThat(o.getClearSpecial()).isEqualTo(o.occurancyOf(Special.CLEARSPECIAL)).isEqualTo(13);
        assertThat(o.getGravity()).isEqualTo(o.occurancyOf(Special.GRAVITY)).isEqualTo(15);
        assertThat(o.getQuakeField()).isEqualTo(o.occurancyOf(Special.QUAKEFIELD)).isEqualTo(17);
        assertThat(o.getBlockBomb()).isEqualTo(o.occurancyOf(Special.BLOCKBOMB)).isEqualTo(20);
    }

    @Test
    public void testOccurancyShouldBeNormalized2() {
        SpecialOccurancy o = SpecialOccurancy.specialOccurancy(s -> {
        });

        assertThat(o.getAddLine()).isEqualTo(o.occurancyOf(Special.ADDLINE)).isEqualTo(12);
        assertThat(o.getClearLine()).isEqualTo(o.occurancyOf(Special.CLEARLINE)).isEqualTo(11);
        assertThat(o.getNukeField()).isEqualTo(o.occurancyOf(Special.NUKEFIELD)).isEqualTo(11);
        assertThat(o.getRandomClear()).isEqualTo(o.occurancyOf(Special.RANDOMCLEAR)).isEqualTo(11);
        assertThat(o.getSwitchField()).isEqualTo(o.occurancyOf(Special.SWITCHFIELD)).isEqualTo(11);
        assertThat(o.getClearSpecial()).isEqualTo(o.occurancyOf(Special.CLEARSPECIAL)).isEqualTo(11);
        assertThat(o.getGravity()).isEqualTo(o.occurancyOf(Special.GRAVITY)).isEqualTo(11);
        assertThat(o.getQuakeField()).isEqualTo(o.occurancyOf(Special.QUAKEFIELD)).isEqualTo(11);
        assertThat(o.getBlockBomb()).isEqualTo(o.occurancyOf(Special.BLOCKBOMB)).isEqualTo(11);
    }

}
