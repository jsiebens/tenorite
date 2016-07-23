package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

/**
 * @author Johan Siebens
 */
public final class NrOfConsecutiveCombos extends AbstractNrOfConsecutiveCombos {

    public NrOfConsecutiveCombos(Badge type, int requiredNrOfCombos, int requiredNrOfConsecutiveGames) {
        super(type, PlayingStats::getNrOfCombos, requiredNrOfCombos, requiredNrOfConsecutiveGames);
    }

}
