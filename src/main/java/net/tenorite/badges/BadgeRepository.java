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

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Johan Siebens
 */
public interface BadgeRepository {

    BadgeOps badgeOps(Tempo tempo);

    interface BadgeOps {

        Optional<BadgeLevel> getBadgeLevel(String name, Badge badge);

        void saveBadgeLevel(BadgeLevel badgeLevel);

        Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name);

        List<BadgeLevel> badgeLevels(Badge badge);

        long getProgress(Badge badge, String name);

        long updateProgress(Badge badge, String name, long value);

    }

}
