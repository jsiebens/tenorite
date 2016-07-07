package net.tenorite.modes;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.validators.GameWonWithSpecificSpecial;
import net.tenorite.badges.validators.NrOfSpecialsUsed;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static net.tenorite.badges.BadgeValidators.*;

@Component
public final class Classic extends GameMode {

    public static final GameModeId ID = GameModeId.of("CLASSIC");

    public static final GameRules RULES = GameRules.gameRules(b -> b.classicRules(true));

    public Classic() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return tempo.equals(Tempo.FAST) ? "Classic TetriFAST" : "Classic TetriNET";
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),

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

            pushingToTheTop(ID),
            theCleaningLady(ID),
            manhattenProject(ID),
            punchedCardMachine(ID),
            switcher(ID),
            thePurifier(ID),
            blackHole(ID),
            tsunami(ID),
            bombSquad(ID)
        );
    }

    private static BadgeValidator theSpecialist(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "THE_SPECIALIST"), s -> true);
    }

    private static BadgeValidator bobTheBuilder(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "BOB_THE_BUILDER"), Special.ADDLINE::equals);
    }

    private static BadgeValidator keepingItClean(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "KEEPING_IT_CLEAN"), Special.CLEARLINE::equals);
    }

    private static BadgeValidator littleBoy(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "LITTLE_BOY"), Special.NUKEFIELD::equals);
    }

    private static BadgeValidator swissCheese(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "SWISS_CHEESE"), Special.RANDOMCLEAR::equals);
    }

    private static BadgeValidator aGift(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "A_GIFT"), Special.SWITCHFIELD::equals);
    }

    private static BadgeValidator noSpecials4U(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "NO_SPECIALS_4U"), Special.CLEARSPECIAL::equals);
    }

    private static BadgeValidator newtonsLaw(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "NEWTON_S_LAW"), Special.GRAVITY::equals);
    }

    private static BadgeValidator shakenNotStirred(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "SHAKEN_NOT_STIRRED"), Special.QUAKEFIELD::equals);
    }

    private static BadgeValidator theTerrorist(GameModeId gameModeId) {
        return new NrOfSpecialsUsed(Badge.of(gameModeId, "THE_TERRORIST"), Special.BLOCKBOMB::equals);
    }

    // =====

    private static BadgeValidator pushingToTheTop(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "PUSHING_TO_THE_TOP"), Special.ADDLINE::equals);
    }

    private static BadgeValidator theCleaningLady(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "THE_CLEANING_LADY"), Special.CLEARLINE::equals);
    }

    private static BadgeValidator manhattenProject(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "MANHATTAN_PROJECT"), Special.NUKEFIELD::equals);
    }

    private static BadgeValidator punchedCardMachine(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "PUNCHED_CARD_MACHINE"), Special.RANDOMCLEAR::equals);
    }

    private static BadgeValidator switcher(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "SWITCHER"), Special.SWITCHFIELD::equals);
    }

    private static BadgeValidator thePurifier(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "THE_PURIFIER"), Special.CLEARSPECIAL::equals);
    }

    private static BadgeValidator blackHole(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "BLACK_HOLE"), Special.GRAVITY::equals);
    }

    private static BadgeValidator tsunami(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "TSUNAMI"), Special.QUAKEFIELD::equals);
    }

    private static BadgeValidator bombSquad(GameModeId gameModeId) {
        return new GameWonWithSpecificSpecial(Badge.of(gameModeId, "BOMB_SQUAD"), Special.BLOCKBOMB::equals);
    }

}
