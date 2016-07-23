package net.tenorite.badges;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class BadgeType {

    @JsonCreator
    public static BadgeType of(String name) {
        return new BadgeTypeBuilder().value(name).build();
    }

    abstract String value();

    @Override
    @JsonValue
    public String toString() {
        return value();
    }

}
