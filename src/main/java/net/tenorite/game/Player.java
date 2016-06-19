package net.tenorite.game;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Value.Immutable
@ImmutableStyle
public abstract class Player {

    public static Player of(int slot, String name, String team) {
        return new PlayerBuilder().slot(slot).name(name).team(ofNullable(team)).build();
    }

    public abstract int getSlot();

    public abstract String getName();

    public abstract Optional<String> getTeam();

    public final boolean isTeamPlayer() {
        return getTeam().filter(t -> !t.trim().isEmpty()).isPresent();
    }

    public final boolean isSoloPlayer() {
        return !isTeamPlayer();
    }

}
