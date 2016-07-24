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
package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepositoryStub;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.GameModeId;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Siebens
 */
public abstract class AbstractValidatorTestCase {

    protected static final GameModeId GAME_MODE_ID = GameModeId.of("JUNIT");

    protected static final Badge BADGE = Badge.of(GAME_MODE_ID, "junit");

    protected final BadgeRepositoryStub badgeRepository = new BadgeRepositoryStub();

    protected final List<BadgeEarned> published = new ArrayList<>();

    @Before
    public final void setUp() {
        badgeRepository.clear();
        published.clear();
    }

}
