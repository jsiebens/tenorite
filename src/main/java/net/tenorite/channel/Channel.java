package net.tenorite.channel;

import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class Channel {

    public static Channel of(GameModeId gameModeId, String name) {
        return new ChannelBuilder().gameModeId(gameModeId).name(name).nrOfPlayers(0).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getName();

    public abstract int getNrOfPlayers();

}
