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
package net.tenorite.tournament;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.bson.types.ObjectId;
import org.immutables.value.Value;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.List;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = TournamentBuilder.ImmutableTournament.class)
public abstract class Tournament {

    public enum State {
        CREATED
    }

    public static Tournament of(String name, GameModeId gameModeId, List<String> participants) {
        return new TournamentBuilder()
            .id(new ObjectId().toString())
            .timestamp(System.currentTimeMillis())
            .name(name)
            .gameModeId(gameModeId)
            .participants(participants)
            .state(State.CREATED)
            .build();
    }

    @MongoObjectId
    @JsonProperty("_id")
    public abstract String getId();

    public abstract long getTimestamp();

    public abstract String getName();

    public abstract GameModeId getGameModeId();

    public abstract List<String> getParticipants();

    public abstract State getState();

}
