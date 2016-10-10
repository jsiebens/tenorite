package net.tenorite.tournament;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = ScoreBuilder.ImmutableScore.class)
public abstract class Score {

    public static Score of(String name, int score) {
        return new ScoreBuilder().name(name).score(score).build();
    }

    public abstract String getName();

    public abstract int getScore();

    Score incr() {
        return new ScoreBuilder().name(getName()).score(getScore() + 1).build();
    }

}
