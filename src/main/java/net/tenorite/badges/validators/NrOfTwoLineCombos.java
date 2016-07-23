package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

/**
 * @author Johan Siebens
 */
public final class NrOfTwoLineCombos extends AbstractNrOfCombos {

    public NrOfTwoLineCombos(Badge type) {
        super(type, PlayingStats::getNrOfTwoLineCombos);
    }

}
