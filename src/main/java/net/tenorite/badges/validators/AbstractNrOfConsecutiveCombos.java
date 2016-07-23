package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Johan Siebens
 */
abstract class AbstractNrOfConsecutiveCombos extends BadgeValidator {

    private final Function<PlayingStats, Integer> nrOfCombos;

    private final int requiredNrOfCombos;

    private final int requiredNrOfConsecutiveGames;

    AbstractNrOfConsecutiveCombos(Badge type, Function<PlayingStats, Integer> nrOfCombos, int requiredNrOfCombos, int requiredNrOfConsecutiveGames) {
        super(type);
        this.nrOfCombos = nrOfCombos;
        this.requiredNrOfCombos = requiredNrOfCombos;
        this.requiredNrOfConsecutiveGames = requiredNrOfConsecutiveGames;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats playingStats, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        String name = playingStats.getPlayer().getName();
        int combos = nrOfCombos.apply(playingStats);

        if (combos >= requiredNrOfCombos) {
            long count = badgeOps.getProgress(badge, name) + 1;
            updateBadgeLevelAndProgressWhenTargetIsReached(game, name, badge, count, requiredNrOfConsecutiveGames, badgeOps, onBadgeEarned);
        }
        else {
            badgeOps.updateProgress(badge, name, 0);
        }
    }

}
