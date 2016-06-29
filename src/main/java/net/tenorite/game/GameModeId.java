package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public final class GameModeId implements Comparable<GameModeId> {

    @JsonCreator
    public static GameModeId of(String name) {
        return new GameModeId(name);
    }

    private final String value;

    private GameModeId(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(GameModeId o) {
        return value.compareTo(o.value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && Objects.equals(value, ((GameModeId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
