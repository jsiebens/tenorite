package net.tenorite.badges;

import net.tenorite.badges.validators.NrOfConsecutiveGamesLost;
import net.tenorite.badges.validators.NrOfConsecutiveGamesWon;
import net.tenorite.badges.validators.NrOfGamesPlayed;
import net.tenorite.badges.validators.NrOfGamesWon;
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

}
