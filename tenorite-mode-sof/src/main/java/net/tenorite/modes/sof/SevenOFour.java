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
package net.tenorite.modes.sof;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerWonMessage;
import net.tenorite.util.Scheduler;

import java.util.*;
import java.util.function.Consumer;

import static net.tenorite.badges.validators.BadgeValidators.*;
import static net.tenorite.game.PlayingStats.*;

/**
 * @author Johan Siebens
 */
public final class SevenOFour extends GameMode {

    private static final Comparator<PlayingStats> BY_FOUR_LINE_COMBOS = (o1, o2) -> o1.getNrOfFourLineCombos() - o2.getNrOfFourLineCombos();

    private static final Comparator<PlayingStats> COMPARATOR =
        BY_FOUR_LINE_COMBOS.reversed() // most four line combos first
            .thenComparing(BY_COMBOS.reversed()) // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    public static final GameModeId ID = GameModeId.of("SOF");

    private static final GameRules RULES = GameRules.gameRules(b -> b
        .classicRules(false)
        .specialAdded(0)
        .specialCapacity(0)
    );

    public SevenOFour() {
        super(ID, RULES);
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new Listener(channel).and(new SuddenDeath(600, 10, 1, scheduler, channel));
    }

    @Override
    public Comparator<PlayingStats> getPlayingStatsComparator() {
        return COMPARATOR;
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),
            fastAndFurious(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID)
        );
    }

    private static final class Listener implements GameListener {

        private static final int TARGET = 7;

        private final Consumer<Message> channel;

        private Map<Integer, Integer> nrOfFourLines = new HashMap<>();

        Listener(Consumer<Message> channel) {
            this.channel = channel;
        }

        @Override
        public void onClassicStyleAdd(Player sender, int lines) {
            if (lines == 4 && incr(sender.getSlot()) >= TARGET) {
                channel.accept(PlayerWonMessage.of(sender.getSlot()));
            }
        }

        private int incr(int sender) {
            return nrOfFourLines.compute(sender, (k, v) -> v == null ? 1 : v + 1);
        }

    }

}
