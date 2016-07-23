package net.tenorite.badges;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;

import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
public class BadgeRepositoryStub implements BadgeRepository, BadgeRepository.BadgeOps {

    private Map<String, Map<Badge, BadgeLevel>> badges = new HashMap<>();

    private Map<String, Long> data = new HashMap<>();

    @Override
    public BadgeOps badgeOps(Tempo tempo) {
        return this;
    }

    @Override
    public Optional<BadgeLevel> getBadgeLevel(String name, Badge badge) {
        return ofNullable(badges.get(name)).flatMap(m -> ofNullable(m.get(badge)));
    }

    @Override
    public void saveBadgeLevel(BadgeLevel badgeLevel) {
        GameModeId gameModeId = badgeLevel.getGameModeId();
        BadgeType badgeType = badgeLevel.getBadgeType();
        Badge badge = Badge.of(gameModeId, badgeType);

        badges.computeIfAbsent(badgeLevel.getName(), s -> new HashMap<>()).put(badge, badgeLevel);
    }

    @Override
    public Map<Badge, BadgeLevel> badgeLevels(GameModeId gameModeId, String name) {
        return badges.getOrDefault(name, Collections.emptyMap());
    }

    @Override
    public List<BadgeLevel> badgeLevels(Badge badge) {
        throw new UnsupportedOperationException("implement me!");
    }

    @Override
    public long getProgress(Badge badge, String name) {
        return data.getOrDefault(badge + "|" + name, 0L);
    }

    @Override
    public long updateProgress(Badge badge, String name, long value) {
        data.put(badge + "|" + name, value);
        return value;
    }

    public void clear() {
        badges.clear();
        data.clear();
    }

}
