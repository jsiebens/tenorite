package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Special;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Johan Siebens
 */
public final class GameWonWithSpecificSpecial extends BadgeValidator {

    private final Predicate<Special> specialPredicate;

    public GameWonWithSpecificSpecial(Badge type, Predicate<Special> specialPredicate) {
        super(type);
        this.specialPredicate = specialPredicate;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        Game game = gameFinished.getGame();

        PlayingStats first = gameFinished.getRanking().get(0);
        PlayingStats second = gameFinished.getRanking().get(1);

        if (!first.getPlayer().isTeamPlayerOf(second.getPlayer())) {
            String name = first.getPlayer().getName();
            int target = first.getTotalNrOfSpecialsUsed(specialPredicate);
            int other = first.getTotalNrOfSpecialsUsed(specialPredicate.negate());

            if (target != 0 && other == 0) {
                long nextLevel = badgeOps.getProgress(badge, name) + 1;
                updateBadgeLevel(game, name, badge, nextLevel, badgeOps, onBadgeEarned);
            }
        }
    }

}
