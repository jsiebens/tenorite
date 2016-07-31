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
package net.tenorite.modes.gbomb;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameListener;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.tenorite.badges.validators.BadgeValidators.*;
import static net.tenorite.game.SpecialOccurancy.specialOccurancy;

/**
 * @author Johan Siebens
 */
public final class GBomb extends GameMode {

    public static final GameModeId ID = GameModeId.of("GBOMB");

    private static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(false)
            .linesPerSpecial(2)
            .specialCapacity(5)
            .specialOccurancy(
                specialOccurancy(o -> o
                    .blockBomb(85)
                    .gravity(15)
                )
            )
        );

    public GBomb() {
        super(ID, RULES);
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new SuddenDeath(240, 5, 1, scheduler, channel);
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

            newtonsLaw(ID),
            theTerrorist(ID),

            blackHole(ID),
            bombSquad(ID),

            thePurist(ID),

            closeCall(ID, Special.GRAVITY),
            nuclearLaunch(ID, 5)
        );
    }

}
