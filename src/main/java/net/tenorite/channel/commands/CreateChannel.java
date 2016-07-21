package net.tenorite.channel.commands;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class CreateChannel {

    public static CreateChannel of(Tempo tempo, GameMode gameMode, String name, boolean ephemeral) {
        return new CreateChannelBuilder().tempo(tempo).gameMode(gameMode).name(name).ephemeral(ephemeral).build();
    }

    public abstract Tempo getTempo();

    public abstract GameMode getGameMode();

    public abstract String getName();

    public abstract boolean isEphemeral();

}
