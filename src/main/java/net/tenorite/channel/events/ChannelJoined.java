package net.tenorite.channel.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class ChannelJoined {

    public static ChannelJoined of(Tempo tempo, GameMode gameMode, String channel, String name) {
        return new ChannelJoinedBuilder().tempo(tempo).gameMode(gameMode).channel(channel).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract GameMode getGameMode();

    public abstract String getChannel();

    public abstract String getName();

}
