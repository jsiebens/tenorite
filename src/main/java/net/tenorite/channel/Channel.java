package net.tenorite.channel;

import net.tenorite.game.GameMode;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class Channel {

    public static Channel of(GameMode gameMode, String name) {
        return new ChannelBuilder().gameMode(gameMode).name(name).nrOfPlayers(0).build();
    }

    public abstract GameMode getGameMode();

    public abstract String getName();

    public abstract int getNrOfPlayers();

}
