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
package net.tenorite.modes.classic;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.validators.NrOfBombsDetonated;
import net.tenorite.badges.validators.SpecialWords;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.tenorite.badges.BadgeValidators.*;

/**
 * @author Johan Siebens
 */
@Component
public final class Classic extends GameMode {

    public static final GameModeId ID = GameModeId.of("CLASSIC");

    public static final GameRules RULES = GameRules.gameRules(b -> b.classicRules(true));

    public Classic() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return tempo.equals(Tempo.FAST) ? "TetriFAST" : "TetriNET";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return tempo.equals(Tempo.FAST) ? "classic TetriFAST" : "classic TetriNET";
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        List<BadgeValidator> validators = new ArrayList<>(asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),
            fastAndFurious(ID),

            comboWombo(ID),
            doubleTrouble(ID),
            threeIsCompany(ID),
            fourOClock(ID),

            comboStrike(ID),
            doubleStrike(ID),
            tripleStrike(ID),
            quadrupleStrike(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID),

            theSpecialist(ID),
            bobTheBuilder(ID),
            keepingItClean(ID),
            littleBoy(ID),
            swissCheese(ID),
            aGift(ID),
            noSpecials4U(ID),
            newtonsLaw(ID),
            shakenNotStirred(ID),
            theTerrorist(ID),
            handyMan(ID),

            pushingToTheTop(ID),
            theCleaningLady(ID),
            manhattenProject(ID),
            punchedCardMachine(ID),
            switcher(ID),
            thePurifier(ID),
            blackHole(ID),
            tsunami(ID),
            bombSquad(ID),
            thePurist(ID),

            hitchhikersGuideToTheSpecialist(ID),
            hitchhikersGuideToComboWombo(ID),
            hitchhikersGuideToEliminator(ID),
            hitchhikersGuideToVictory(ID),

            tooEasyForMe(ID),
            nuclearLaunch(ID, 3),
            grandTheft(ID),
            closeCall(ID, Special.NUKEFIELD)
        ));

        validators.addAll(words(ID));

        return validators;
    }

    private static BadgeValidator tooEasyForMe(GameModeId gameModeId) {
        return new TooEasyForMe(Badge.of(gameModeId, "TOO_EASY_FOR_ME"));
    }

    private static BadgeValidator grandTheft(GameModeId gameModeId) {
        return new GrandTheft(Badge.of(gameModeId, "GRAND_THEFT"));
    }

    private static List<BadgeValidator> words(GameModeId gameModeId) {
        return Stream
            .of(
                "arc",
                "bacon",
                "banana",
                "barn",
                "bag",
                "cab",
                "cargo",
                "cobra",
                "crab",
                "gas",
                "groan",
                "orb",
                "orca",
                "rag",
                "sac",
                "scar"
            )
            .sorted(BY_LENGTH.thenComparing(String::compareTo))
            .map(w -> new SpecialWords(Badge.of(gameModeId, "WORDS_" + w.toUpperCase()), w))
            .collect(toList());
    }

    private static Comparator<String> BY_LENGTH = (s1, s2) -> s1.length() - s2.length();

}
