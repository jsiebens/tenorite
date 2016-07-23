package net.tenorite.channel.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class ChannelCreated {

    public static ChannelCreated of(Tempo tempo, GameModeId gameModeId, String name) {
        return new ChannelCreatedBuilder().tempo(tempo).gameModeId(gameModeId).name(name).build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract String getName();

}
