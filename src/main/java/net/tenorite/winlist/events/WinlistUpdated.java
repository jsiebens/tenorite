package net.tenorite.winlist.events;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.util.ImmutableStyle;
import net.tenorite.winlist.WinlistItem;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@ImmutableStyle
public abstract class WinlistUpdated {

    public static WinlistUpdated of(Tempo tempo, GameMode gameMode, List<WinlistItem> items) {
        return new WinlistUpdatedBuilder().tempo(tempo).gameMode(gameMode).items(items).build();
    }

    public abstract Tempo getTempo();

    public abstract GameMode getGameMode();

    public abstract List<WinlistItem> getItems();

}
