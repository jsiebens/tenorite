package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = PlayerBuilder.ImmutablePlayer.class)
public abstract class Player {

    public static Player of(int slot, String name, String team) {
        return new PlayerBuilder().slot(slot).name(name).team(ofNullable(team)).build();
    }

    public abstract int getSlot();

    public abstract String getName();

    public abstract Optional<String> getTeam();

    @JsonIgnore
    public final boolean isTeamPlayer() {
        return getTeam().filter(t -> !t.trim().isEmpty()).isPresent();
    }

    @JsonIgnore
    public final boolean isSoloPlayer() {
        return !isTeamPlayer();
    }

    @JsonIgnore
    public final boolean isTeamPlayerOf(Player other) {
        return isTeamPlayer() && Objects.equals(getTeam(), other.getTeam());
    }

}
