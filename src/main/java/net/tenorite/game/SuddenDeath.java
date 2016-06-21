package net.tenorite.game;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class SuddenDeath {

    public static SuddenDeath of(int delay, int interval, int nrOfLines) {
        return new SuddenDeathBuilder().delay(delay).interval(interval).nrOfLines(nrOfLines).build();
    }

    public abstract int getDelay();

    public abstract int getInterval();

    public abstract int getNrOfLines();

}
