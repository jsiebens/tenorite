package net.tenorite.badges;

import net.tenorite.badges.validators.*;
import net.tenorite.game.GameModeId;

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

}
