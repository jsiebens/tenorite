package net.tenorite.winlist;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;

import java.util.List;
import java.util.Optional;

public interface WinlistRepository {

    WinlistOps winlistOps(Tempo tempo);

    interface WinlistOps {

        Optional<WinlistItem> getWinlistItem(GameModeId mode, WinlistItem.Type type, String name);

        void saveWinlistItem(GameModeId mode, WinlistItem score);

        List<WinlistItem> loadWinlist(GameModeId mode);

    }

}
