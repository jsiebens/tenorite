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
        updateBadgeLevel(game, name, badge, nrOfCombos.apply(playingStats), badgeOps, onBadgeEarned);
    }

}
