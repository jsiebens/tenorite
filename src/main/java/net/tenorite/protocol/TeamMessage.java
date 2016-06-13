package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class TeamMessage implements Message {

    public static TeamMessage of(int sender, String team) {
        return new TeamMessageBuilder().sender(sender).team(team).build();
    }

    public abstract int getSender();

    public abstract String getTeam();

    @Override
    public String raw(Tempo tempo) {
        return String.format("team %s %s", getSender(), getTeam());
    }

}
