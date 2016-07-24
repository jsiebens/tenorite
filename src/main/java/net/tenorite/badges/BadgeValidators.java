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
package net.tenorite.badges;

import net.tenorite.badges.validators.*;
import net.tenorite.core.Special;
import net.tenorite.game.GameModeId;
import net.tenorite.game.PlayingStats;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author Johan Siebens
 */
public class BadgeValidators {

    public static BadgeValidator competitor(GameModeId gameModeId) {
        return new NrOfGamesPlayed(Badge.of(gameModeId, "COMPETITOR"), 10);
    }

    public static BadgeValidator likeAPro(GameModeId gameModeId) {
        return new NrOfGamesWon(Badge.of(gameModeId, "LIKE_A_PRO"), 10);
    }

    public static BadgeValidator likeAKing(GameModeId gameModeId) {
        return new NrOfGamesWon(Badge.of(gameModeId, "LIKE_A_KING"), 1000);
    }

    public static BadgeValidator imOnFire(GameModeId gameModeId) {
        return new NrOfConsecutiveGamesWon(Badge.of(gameModeId, "I_M_ON_FIRE"), 5);
    }

    public static BadgeValidator justKeepTrying(GameModeId gameModeId) {
        return new NrOfConsecutiveGamesLost(Badge.of(gameModeId, "JUST_KEEP_TRYING"), 5);
    }

    public static BadgeValidator fastAndFurious(GameModeId gameModeId) {
        return new GameWonAt10Bpm(Badge.of(gameModeId, "FAST_AND_FURIOUS"));
    }

    public static BadgeValidator comboWombo(GameModeId gameModeId) {
        return new NrOfCombos(Badge.of(gameModeId, "COMBO_WOMBO"));
    }

    public static BadgeValidator doubleTrouble(GameModeId gameModeId) {
        return new NrOfTwoLineCombos(Badge.of(gameModeId, "DOUBLE_TROUBLE"));
    }

    public static BadgeValidator threeIsCompany(GameModeId gameModeId) {
        return new NrOfThreeLineCombos(Badge.of(gameModeId, "THREE_S_COMPANY"));
    }

    public static BadgeValidator fourOClock(GameModeId gameModeId) {
        return new NrOfFourLineCombos(Badge.of(gameModeId, "FOUR_O_CLOCK"));
    }

    public static BadgeValidator comboStrike(GameModeId gameModeId) {
        return new NrOfConsecutiveCombos(Badge.of(gameModeId, "COMBO_STRIKE"), 10, 5);
    }

    public static BadgeValidator doubleStrike(GameModeId gameModeId) {
        return new NrOfConsecutiveTwoLineCombos(Badge.of(gameModeId, "DOUBLE_STRIKE"), 5, 5);
    }

    public static BadgeValidator tripleStrike(GameModeId gameModeId) {
        return new NrOfConsecutiveThreeLineCombos(Badge.of(gameModeId, "TRIPLE_STRIKE"), 3, 5);
    }

    public static BadgeValidator quadrupleStrike(GameModeId gameModeId) {
        return new NrOfConsecutiveFourLineCombos(Badge.of(gameModeId, "QUADRUPLE_STRIKE"), 2, 5);
    }

    public static BadgeValidator eliminator(GameModeId gameModeId) {
        return new NrOfLines(Badge.of(gameModeId, "ELIMINATOR"), 1000);
    }

    public static BadgeValidator eradicator(GameModeId gameModeId) {
        return new NrOfLines(Badge.of(gameModeId, "ERADICATOR"), 100000);
    }

    public static BadgeValidator dropsInTheBucket(GameModeId gameModeId) {
        return new NrOfBlocks(Badge.of(gameModeId, "DROPS_IN_THE_BUCKET"), 1000);
    }

    public static BadgeValidator dropItLikeItsHot(GameModeId gameModeId) {
        return new NrOfBlocks(Badge.of(gameModeId, "DROP_IT_LIKE_ITS_HOT"), 100000);
    }

    public static BadgeValidator thePurist(GameModeId gameModeId) {
        return new NoSpecialsUsed(Badge.of(gameModeId, "THE_PURIST"));
    }

    public static BadgeValidator theSpecialist(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "THE_SPECIALIST"), s -> true);
    }

    public static BadgeValidator bobTheBuilder(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "BOB_THE_BUILDER"), Special.ADDLINE::equals);
    }

    public static BadgeValidator keepingItClean(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "KEEPING_IT_CLEAN"), Special.CLEARLINE::equals);
    }

    public static BadgeValidator littleBoy(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "LITTLE_BOY"), Special.NUKEFIELD::equals);
    }

    public static BadgeValidator swissCheese(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "SWISS_CHEESE"), Special.RANDOMCLEAR::equals);
    }

    public static BadgeValidator aGift(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "A_GIFT"), Special.SWITCHFIELD::equals);
    }

    public static BadgeValidator noSpecials4U(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "NO_SPECIALS_4U"), Special.CLEARSPECIAL::equals);
    }

    public static BadgeValidator newtonsLaw(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "NEWTON_S_LAW"), Special.GRAVITY::equals);
    }

    public static BadgeValidator shakenNotStirred(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "SHAKEN_NOT_STIRRED"), Special.QUAKEFIELD::equals);
    }

    public static BadgeValidator theTerrorist(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "THE_TERRORIST"), Special.BLOCKBOMB::equals);
    }

    public static BadgeValidator handyMan(GameModeId gameModeId) {
        return new AllSpecialsUsed(Badge.of(gameModeId, "HANDY_MAN"));
    }

    // =====

    public static BadgeValidator pushingToTheTop(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "PUSHING_TO_THE_TOP"), Special.ADDLINE::equals);
    }

    public static BadgeValidator theCleaningLady(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "THE_CLEANING_LADY"), Special.CLEARLINE::equals);
    }

    public static BadgeValidator manhattenProject(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "MANHATTAN_PROJECT"), Special.NUKEFIELD::equals);
    }

    public static BadgeValidator punchedCardMachine(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "PUNCHED_CARD_MACHINE"), Special.RANDOMCLEAR::equals);
    }

    public static BadgeValidator switcher(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "SWITCHER"), Special.SWITCHFIELD::equals);
    }

    public static BadgeValidator thePurifier(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "THE_PURIFIER"), Special.CLEARSPECIAL::equals);
    }

    public static BadgeValidator blackHole(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "BLACK_HOLE"), Special.GRAVITY::equals);
    }

    public static BadgeValidator tsunami(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "TSUNAMI"), Special.QUAKEFIELD::equals);
    }

    public static BadgeValidator bombSquad(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "BOMB_SQUAD"), Special.BLOCKBOMB::equals);
    }

    public static BadgeValidator hitchhikersGuideToTheSpecialist(GameModeId gameModeId) {
        return new HitchhikersGuide(Badge.of(gameModeId, "HITCHHIKERS_GUIDE_TO_THE_SPECIALIST"), PlayingStats::getTotalNrOfSpecialsUsed);
    }

    public static BadgeValidator hitchhikersGuideToComboWombo(GameModeId gameModeId) {
        return new HitchhikersGuide(Badge.of(gameModeId, "HITCHHIKERS_GUIDE_TO_COMBO_WOMBO"), PlayingStats::getNrOfCombos);
    }

    public static BadgeValidator hitchhikersGuideToEliminator(GameModeId gameModeId) {
        return new HitchhikersGuide(Badge.of(gameModeId, "HITCHHIKERS_GUIDE_TO_ELIMINATOR"), PlayingStats::getNrOfLines);
    }

    public static BadgeValidator hitchhikersGuideToVictory(GameModeId gameModeId) {
        return new GameWonWithHitchhikersGuide(Badge.of(gameModeId, "HITCHHIKERS_GUIDE_TO_VICTORY"), asList(
            PlayingStats::getNrOfLines,
            PlayingStats::getNrOfCombos,
            PlayingStats::getTotalNrOfSpecialsUsed
        ));
    }

    public static BadgeValidator closeCall(GameModeId gameModeId, Special special, Special... extra) {
        Set<Special> specials = new HashSet<>();
        specials.add(special);
        specials.addAll(asList(extra));
        return new SpecialUsedBeforeDeath(Badge.of(gameModeId, "CLOSE_CALL"), specials);
    }

}
