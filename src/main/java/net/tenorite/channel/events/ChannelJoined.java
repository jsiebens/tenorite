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
package net.tenorite.channel.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class ChannelJoined {

    public static ChannelJoined of(Tempo tempo, GameModeId gameModeId, String channel, String name) {
        return new ChannelJoinedBuilder().tempo(tempo).gameModeId(gameModeId).channel(channel).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract String getChannel();

    public abstract String getName();

}
