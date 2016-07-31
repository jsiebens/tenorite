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
package net.tenorite.badges;

import net.tenorite.game.GameModeId;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class BadgeTest {

    @Test
    public void testTitleAndDescription() {
        Badge badge = Badge.of(GameModeId.of("TEST"), "BADGE_A");

        assertThat(badge.getTitle()).isEqualTo("Title of Badge A");
        assertThat(badge.getDescription()).isEqualTo("Description of Badge A");
    }

    @Test
    public void testTitleAndDescriptionWhenBadgesPropertiesFileIsMissing() {
        Badge badge = Badge.of(GameModeId.of("UNKNOWN"), "BADGE_A");

        assertThat(badge.getTitle()).isEqualTo("badge.BADGE_A.title");
        assertThat(badge.getDescription()).isEqualTo("badge.BADGE_A.description");
    }

}
