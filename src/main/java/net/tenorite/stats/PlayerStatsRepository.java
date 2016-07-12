package net.tenorite.stats;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.game.PlayingStats;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsRepository {

    PlayerStatsOps playerStatsOps(Tempo tempo);

    interface PlayerStatsOps {

        void updateStats(GameModeId gameModeId, PlayingStats playingStats, boolean winner);

        Optional<PlayerStats> playerStats(GameModeId gameModeId, String name);

    }

}
