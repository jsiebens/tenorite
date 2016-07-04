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

            long count = badgeOps.getProgress(type, name) + 1;

            if (count >= target) {
                Optional<BadgeLevel> opt = badgeOps.getBadgeLevel(name, type);
                long newLevel = opt.map(BadgeLevel::getLevel).orElse(0L) + 1;

                BadgeLevel badge = BadgeLevel.of(name, type, game.getTimestamp(), newLevel, game.getId());

                badgeOps.saveBadgeLevel(badge);
                badgeOps.updateProgress(type, name, count % target);

                onBadgeEarned.accept(BadgeEarned.of(badge, opt.isPresent()));
            }
            else {
                badgeOps.updateProgress(type, name, count);
            }

            gameFinished.getRanking().stream().skip(1).forEach(p -> badgeOps.updateProgress(type, p.getPlayer().getName(), 0));
        }

    }

}
