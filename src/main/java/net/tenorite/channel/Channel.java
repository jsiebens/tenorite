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
package net.tenorite.channel;

import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class Channel {

    public static Channel of(GameModeId gameModeId, String name, int nrOfPlayers) {
        return new ChannelBuilder().gameModeId(gameModeId).name(name).nrOfPlayers(nrOfPlayers).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getName();

    public abstract int getNrOfPlayers();

}
