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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeBuilder.ImmutableBadge.class)
public abstract class Badge {

    public static Badge of(GameModeId gameModeId, String badgeId) {
        return new BadgeBuilder().gameModeId(gameModeId).badgeId(badgeId).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getBadgeId();

    @Value.Lazy
    @JsonIgnore
    public String getTitle() {
        String key = "badge." + getBadgeId() + ".title";
        return getGameModeId().getProperty(key).orElse(key);
    }

    @Value.Lazy
    @JsonIgnore
    public String getDescription() {
        String key = "badge." + getBadgeId() + ".description";
        return getGameModeId().getProperty(key).orElse(key);
    }

}
