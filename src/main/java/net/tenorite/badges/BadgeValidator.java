package net.tenorite.badges;

import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.events.GameFinished;

import java.util.function.Consumer;

public abstract class BadgeValidator {

    protected final Badge badge;

    public BadgeValidator(Badge badge) {
        this.badge = badge;
    }

    public Badge getBadge() {
        return badge;
    }

    public void process(GameFinished gameFinished, BadgeRepository badgeRepository, Consumer<BadgeEarned> onBadgeEarned) {
        if (gameFinished.getGame().getGameModeId().equals(badge.getGameModeId()) && gameFinished.getRanking().size() >= 3) {
            doProcess(gameFinished, badgeRepository.badgeOps(gameFinished.getGame().getTempo()), onBadgeEarned);
        }
    }

    protected abstract void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned);

}
