package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

/**
 * @author Johan Siebens
 */
public final class NrOfConsecutiveTwoLineCombos extends AbstractNrOfConsecutiveCombos {

    public NrOfConsecutiveTwoLineCombos(Badge type, int requiredNrOfCombos, int requiredNrOfConsecutiveGames) {
        super(type, PlayingStats::getNrOfTwoLineCombos, requiredNrOfCombos, requiredNrOfConsecutiveGames);
    }

}
