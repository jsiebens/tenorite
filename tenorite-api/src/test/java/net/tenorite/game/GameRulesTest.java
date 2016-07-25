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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameRulesTest {

    @Test
    public void testDefaultRules() {
        String s = GameRules.defaultGameRules().toString();
        assertThat(s).isEqualTo("0 0 2 1 1 1 18 1111111111111112222222222222223333333333333344444444444444555555555555556666666666666677777777777777 1111111111111111111111222222222222222222344444444444444445666666666666666777778888888888889999999999 1 0");
    }

}
