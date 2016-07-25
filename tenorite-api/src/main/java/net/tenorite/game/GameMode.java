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

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.tenorite.game.PlayingStats.*;

/**
 * @author Johan Siebens
 */
public abstract class GameMode {

    private static final Comparator<PlayingStats> DEFAULT_COMPARATOR =
        BY_LEVEL.reversed() // highest levels first
            .thenComparing(BY_COMBOS.reversed()) // most combos first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    private final GameModeId id;

    private final GameRules gameRules;

    protected GameMode(GameModeId id, GameRules gameRules) {
        this.id = id;
        this.gameRules = gameRules;
    }

    public final GameModeId getId() {
        return id;
    }

    public final GameRules getGameRules() {
        return gameRules;
    }

    public String getTitle(Tempo tempo) {
        return id.toString();
    }

    public String getDescription(Tempo tempo) {
        return "";
    }

    public Comparator<PlayingStats> getPlayingStatsComparator() {
        return DEFAULT_COMPARATOR;
    }

    public List<BadgeValidator> getBadgeValidators() {
        return emptyList();
    }

    public final List<Badge> getBadges() {
        return getBadgeValidators().stream().map(BadgeValidator::getBadge).collect(toList());
    }

    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new SuddenDeath(300, 10, 1, scheduler, channel);
    }

}
