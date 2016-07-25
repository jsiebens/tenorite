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
package net.tenorite.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.core.Special;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Map;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = PlayerStatsBuilder.ImmutablePlayerStats.class)
public abstract class PlayerStats {

    public static PlayerStats of(GameModeId gameModeId, String name) {
        return new PlayerStatsBuilder().gameModeId(gameModeId).name(name).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getName();

    @Value.Default
    public long getGamesPlayed() {
        return 0;
    }

    @Value.Default
    public long getGamesWon() {
        return 0;
    }

    @Value.Default
    public long getTimePlayed() {
        return 0;
    }

    @Value.Default
    public int getNrOfBlocks() {
        return 0;
    }

    @Value.Default
    public int getNrOfLines() {
        return 0;
    }

    @Value.Default
    public int getNrOfTwoLineCombos() {
        return 0;
    }

    @Value.Default
    public int getNrOfThreeLineCombos() {
        return 0;
    }

    @Value.Default
    public int getNrOfFourLineCombos() {
        return 0;
    }

    public abstract Map<Special, Integer> getNrOfSpecialsReceived();

    public abstract Map<Special, Integer> getNrOfSpecialsOnOpponent();

    public abstract Map<Special, Integer> getNrOfSpecialsOnTeamPlayer();

    public abstract Map<Special, Integer> getNrOfSpecialsOnSelf();

    @Value.Lazy
    public int getTotalNrOfSpecialsReceived() {
        return getNrOfSpecialsReceived().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnOpponent() {
        return getNrOfSpecialsOnOpponent().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnTeamPlayer() {
        return getNrOfSpecialsOnTeamPlayer().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsOnSelf() {
        return getNrOfSpecialsOnSelf().values().stream().mapToInt(i -> i).sum();
    }

    @Value.Lazy
    public int getTotalNrOfSpecialsUsed() {
        return getTotalNrOfSpecialsOnOpponent() + getTotalNrOfSpecialsOnTeamPlayer() + getTotalNrOfSpecialsOnSelf();
    }

}
