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

public final class HitchhikersGuide extends BadgeValidator {

    static final int THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE = 42;

    private final Function<PlayingStats, Integer> function;

    public HitchhikersGuide(Badge type, Function<PlayingStats, Integer> function) {
        super(type);
        this.function = function;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats playingStats, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        String name = playingStats.getPlayer().getName();
        if (function.apply(playingStats) == THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE) {
            long nextLevel = badgeOps.getProgress(badge, name) + 1;
            updateBadgeLevel(game, name, badge, nextLevel, badgeOps, onBadgeEarned);
        }
    }

}
