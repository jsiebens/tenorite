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
package net.tenorite.modes.sprint;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerWonMessage;
import net.tenorite.util.Scheduler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static net.tenorite.badges.validators.BadgeValidators.*;
import static net.tenorite.game.PlayingStats.*;

/**
 * @author Johan Siebens
 */
public final class Sprint extends GameMode {

    public static final GameModeId ID = GameModeId.of("SPRINT");

    private static final GameRules RULES = GameRules.gameRules(b -> b
        .classicRules(false)
        .linesPerLevel(Constants.LINES_PER_LEVEL)
        .levelIncrease(1)
        .specialAdded(0)
        .specialCapacity(0)
    );

    private static final Comparator<PlayingStats> COMPARATOR =
        BY_LEVEL.reversed() // highest levels first
            .thenComparing(BY_COMBOS.reversed()) // most combos first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    public Sprint() {
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
            dropItLikeItsHot(ID),

            photoFinish(ID),
            eatMyDust(ID)
        );
    }

    private static BadgeValidator eatMyDust(GameModeId gameModeId) {
        return new EatMyDust(Badge.of(gameModeId, "EAT_MY_DUST"));
    }

    private static BadgeValidator photoFinish(GameModeId gameModeId) {
        return new PhotoFinish(Badge.of(gameModeId, "PHOTO_FINISH"));
    }

    private static class Listener implements GameListener {

        private final Consumer<Message> channel;

        Listener(Consumer<Message> channel) {
            this.channel = channel;
        }

        @Override
        public void onLevelUpdate(Player sender, int level) {
            if (level >= 40) {
                channel.accept(PlayerWonMessage.of(sender.getSlot()));
            }
        }

    }

}
