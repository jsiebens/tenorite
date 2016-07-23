package net.tenorite.modes.classic;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Special;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * @author Johan Siebens
 */
class HandyMan extends BadgeValidator {

    HandyMan(Badge badge) {
        super(badge);
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats playingStats, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        long nrOfSpecialsNotUsed = stream(Special.values()).filter(s -> playingStats.getTotalNrOfSpecialsUsed(s::equals) == 0).count();

        if (nrOfSpecialsNotUsed == 0) {
            String name = playingStats.getPlayer().getName();
            long newLevel = badgeOps.getProgress(badge, name) + 1;
            updateBadgeLevel(game, name, badge, newLevel, badgeOps, onBadgeEarned);
        }
    }

}
