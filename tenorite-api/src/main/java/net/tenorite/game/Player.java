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
package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = PlayerBuilder.ImmutablePlayer.class)
public abstract class Player {

    public static Player of(int slot, String name, String team) {
        return new PlayerBuilder().slot(slot).name(name).team(ofNullable(team)).build();
    }

    public abstract int getSlot();

    public abstract String getName();

    public abstract Optional<String> getTeam();

    @JsonIgnore
    public final boolean isTeamPlayer() {
        return getTeam().filter(t -> !t.trim().isEmpty()).isPresent();
    }

    @JsonIgnore
    public final boolean isSoloPlayer() {
        return !isTeamPlayer();
    }

    @JsonIgnore
    public final boolean isTeamPlayerOf(Player other) {
        return isTeamPlayer() && Objects.equals(getTeam(), other.getTeam());
    }

}
