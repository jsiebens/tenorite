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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeBuilder.ImmutableBadge.class)
public abstract class Badge {

    public static Badge of(GameModeId gameModeId, BadgeType badgeType) {
        return new BadgeBuilder().gameModeId(gameModeId).badgeType(badgeType).build();
    }

    public static Badge of(GameModeId gameModeId, String badgeId) {
        return new BadgeBuilder().gameModeId(gameModeId).badgeType(BadgeType.of(badgeId)).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract BadgeType getBadgeType();

    @Value.Lazy
    public String getTitle() {
        String key = getBadgeType() + ".title";
        return properties(getGameModeId()).getProperty(key, key);
    }

    @Value.Lazy
    public String getDescription() {
        String key = getBadgeType() + ".description";
        return properties(getGameModeId()).getProperty(key, key);
    }

    private static final Map<GameModeId, Properties> PROPERTIES = new ConcurrentHashMap<>();

    private static Properties properties(GameModeId gameModeId) {
        return PROPERTIES.computeIfAbsent(gameModeId, g -> {
            try {
                return PropertiesLoaderUtils.loadAllProperties("" + g + "_badges.properties");
            }
            catch (IOException e) {
                return new Properties();
            }
        });
    }
}
