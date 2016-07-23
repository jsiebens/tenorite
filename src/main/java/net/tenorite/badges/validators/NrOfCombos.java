package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

/**
 * @author Johan Siebens
 */
public final class NrOfCombos extends AbstractNrOfCombos {

    public NrOfCombos(Badge type) {
        super(type, PlayingStats::getNrOfCombos);
    }
    
}
