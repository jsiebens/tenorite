package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

/**
 * @author Johan Siebens
 */
public final class NrOfConsecutiveThreeLineCombos extends AbstractNrOfConsecutiveCombos {

    public NrOfConsecutiveThreeLineCombos(Badge type, int requiredNrOfCombos, int requiredNrOfConsecutiveGames) {
        super(type, PlayingStats::getNrOfThreeLineCombos, requiredNrOfCombos, requiredNrOfConsecutiveGames);
    }

}
