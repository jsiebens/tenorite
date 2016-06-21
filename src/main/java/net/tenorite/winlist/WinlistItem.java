package net.tenorite.winlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = WinlistItemBuilder.ImmutableWinlistItem.class)
public abstract class WinlistItem {

    public static WinlistItem of(Type type, String name, long score, long timestamp) {
        return new WinlistItemBuilder().type(type).name(name).score(score).timestamp(timestamp).build();
    }

    public enum Type {

        PLAYER('p'),

        TEAM('t');

        private char letter;

        Type(char letter) {
            this.letter = letter;
        }

        public char getLetter() {
            return letter;
        }

    }

    public abstract Type getType();

    public abstract String getName();

    public abstract long getScore();

    public abstract long getTimestamp();

}
