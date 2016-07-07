package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

public final class NrOfConsecutiveFourLineCombos extends AbstractNrOfConsecutiveCombos {

    public NrOfConsecutiveFourLineCombos(Badge type, int requiredNrOfCombos, int requiredNrOfConsecutiveGames) {
        super(type, PlayingStats::getNrOfFourLineCombos, requiredNrOfCombos, requiredNrOfConsecutiveGames);
    }

}
