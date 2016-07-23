package net.tenorite.channel.commands;

import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class CreateChannel {

    public static CreateChannel of(GameModeId gameModeId, String name, boolean ephemeral) {
        return new CreateChannelBuilder().gameModeId(gameModeId).name(name).ephemeral(ephemeral).build();
    }

    public abstract GameModeId getGameModeId();

    public abstract String getName();

    public abstract boolean isEphemeral();

}
