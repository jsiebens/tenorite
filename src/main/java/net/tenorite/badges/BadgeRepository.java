package net.tenorite.badges;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BadgeRepository {

    BadgeOps badgeOps(Tempo tempo);

    interface BadgeOps {

        Optional<BadgeLevel> getBadgeLevel(String name, Badge badge);

        void saveBadgeLevel(BadgeLevel badgeLevel);

        Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name);

        List<BadgeLevel> badgeLevels(Badge badge);

        long getProgress(Badge badge, String name);

        long updateProgress(Badge badge, String name, long value);

    }

}
