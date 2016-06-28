package net.tenorite.channel.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ChannelJoined {

    public static ChannelJoined of(Tempo tempo, GameModeId gameModeId, String channel, String name) {
        return new ChannelJoinedBuilder().tempo(tempo).gameModeId(gameModeId).channel(channel).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract String getChannel();

    public abstract String getName();

}
