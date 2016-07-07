package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.Optional;
import java.util.function.Consumer;

public final class NrOfConsecutiveGamesWon extends BadgeValidator {

    private final int target;

    public NrOfConsecutiveGamesWon(Badge type, int target) {
        super(type);
        this.target = target;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        Game game = gameFinished.getGame();

        PlayingStats first = gameFinished.getRanking().get(0);
        PlayingStats second = gameFinished.getRanking().get(1);

        if (!first.getPlayer().isTeamPlayerOf(second.getPlayer())) {
            String name = first.getPlayer().getName();

            long count = badgeOps.getProgress(badge, name) + 1;

            updateBadgeLevelAndProgressWhenTargetIsReached(game, name, badge, count, target, badgeOps, onBadgeEarned);

            gameFinished.getRanking().stream().skip(1).forEach(p -> badgeOps.updateProgress(badge, p.getPlayer().getName(), 0));
        }

    }

}
