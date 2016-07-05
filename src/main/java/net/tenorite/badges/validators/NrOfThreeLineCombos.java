package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.game.PlayingStats;

public final class NrOfThreeLineCombos extends AbstractNrOfCombos {

    public NrOfThreeLineCombos(Badge type) {
        super(type, PlayingStats::getNrOfThreeLineCombos);
    }
    
}
