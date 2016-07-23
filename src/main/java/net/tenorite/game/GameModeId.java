package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class GameModeId implements Comparable<GameModeId> {

    @JsonCreator
    public static GameModeId of(String name) {
        return new GameModeIdBuilder().value(name).build();
    }

    abstract String value();

    @Override
    public int compareTo(GameModeId o) {
        return value().compareTo(o.value());
    }

    @Override
    @JsonValue
    public String toString() {
        return value();
    }

}
