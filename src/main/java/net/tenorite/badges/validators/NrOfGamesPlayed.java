package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.function.Consumer;

/**
 * @author Johan Siebens
 */
public final class NrOfGamesPlayed extends BadgeValidator {

    private final int target;

    public NrOfGamesPlayed(Badge badgeType, int target) {
        super(badgeType);
        this.target = target;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats p, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        String name = p.getPlayer().getName();
        long count = badgeOps.getProgress(badge, name) + 1;
        updateBadgeLevelAndProgressWhenTargetIsReached(game, name, badge, count, target, badgeOps, onBadgeEarned);
    }

}
