package net.tenorite.winlist.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.util.ImmutableStyle;
import net.tenorite.winlist.WinlistItem;
import org.immutables.value.Value;

import java.util.List;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class WinlistUpdated {

    public static WinlistUpdated of(Tempo tempo, GameModeId gameModeId, List<WinlistItem> items) {
        return new WinlistUpdatedBuilder().tempo(tempo).gameModeId(gameModeId).items(items).build();
    }

    public abstract Tempo getTempo();

    public abstract GameModeId getGameModeId();

    public abstract List<WinlistItem> getItems();

}
