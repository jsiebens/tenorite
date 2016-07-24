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

import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
public class BadgeRepositoryStub implements BadgeRepository, BadgeRepository.BadgeOps {

    private Map<String, Map<Badge, BadgeLevel>> badges = new HashMap<>();

    private Map<String, Long> data = new HashMap<>();

    @Override
    public BadgeOps badgeOps(Tempo tempo) {
        return this;
    }

    @Override
    public Optional<BadgeLevel> getBadgeLevel(String name, Badge badge) {
        return ofNullable(badges.get(name)).flatMap(m -> ofNullable(m.get(badge)));
    }

    @Override
    public void saveBadgeLevel(BadgeLevel badgeLevel) {
        Badge badge = badgeLevel.getBadge();
        badges.computeIfAbsent(badgeLevel.getName(), s -> new HashMap<>()).put(badge, badgeLevel);
    }

    @Override
    public Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name) {
        return badges.getOrDefault(name, Collections.emptyMap());
    }

    @Override
    public List<BadgeLevel> badgeLevels(Badge badge) {
        throw new UnsupportedOperationException("implement me!");
    }

    @Override
    public long getProgress(Badge badge, String name) {
        return data.getOrDefault(badge + "|" + name, 0L);
    }

    @Override
    public long updateProgress(Badge badge, String name, long value) {
        data.put(badge + "|" + name, value);
        return value;
    }

    public void clear() {
        badges.clear();
        data.clear();
    }

}
