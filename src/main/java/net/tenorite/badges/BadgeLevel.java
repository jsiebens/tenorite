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
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BadgeLevelBuilder.ImmutableBadgeLevel.class)
public abstract class BadgeLevel {

    public static BadgeLevel of(Tempo tempo, Badge badge, String name, long timestamp, long level, String gameId) {
        return
            new BadgeLevelBuilder()
                .tempo(tempo)
                .gameModeId(badge.getGameModeId())
                .badgeType(badge.getBadgeType())
                .name(name)
                .timestamp(timestamp)
                .level(level)
                .gameId(gameId)
                .build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract BadgeType getBadgeType();

    public abstract String getName();

    public abstract long getTimestamp();

    public abstract long getLevel();

    public abstract String getGameId();

}
