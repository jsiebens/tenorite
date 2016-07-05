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
import java.util.function.Function;

abstract class AbstractNrOfCombos extends BadgeValidator {

    private final Function<PlayingStats, Integer> nrOfCombos;

    AbstractNrOfCombos(Badge type, Function<PlayingStats, Integer> nrOfCombos) {
        super(type);
        this.nrOfCombos = nrOfCombos;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats playingStats, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        String name = playingStats.getPlayer().getName();
        int combos = nrOfCombos.apply(playingStats);

        if (combos != 0) {
            Optional<BadgeLevel> optBadgeLevel = badgeOps.getBadgeLevel(name, getBadge());
            long currentLevel = optBadgeLevel.isPresent() ? optBadgeLevel.get().getLevel() : 0;
            if (combos > currentLevel) {
                BadgeLevel badgeLevel = BadgeLevel.of(name, badge, game.getTimestamp(), combos, game.getId());

                badgeOps.saveBadgeLevel(badgeLevel);
                badgeOps.updateProgress(badge, name, combos);

                onBadgeEarned.accept(BadgeEarned.of(badgeLevel, optBadgeLevel.isPresent()));
            }
        }
    }

}
