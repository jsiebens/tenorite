package net.tenorite.winlist;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;

import java.util.List;
import java.util.Optional;

public interface WinlistRepository {

    WinlistOps winlistOps(Tempo tempo);

    interface WinlistOps {

        Optional<WinlistItem> getWinlistItem(GameMode mode, WinlistItem.Type type, String name);

        void saveWinlistItem(GameMode mode, WinlistItem score);

        List<WinlistItem> loadWinlist(GameMode mode);

    }

}
