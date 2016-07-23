package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public final class SpecialWords extends BadgeValidator {

    private final String word;

    public SpecialWords(Badge badge, String word) {
        super(badge);
        this.word = word;
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        gameFinished.getRanking().forEach(p -> validateBadge(gameFinished.getGame(), p, badgeOps, onBadgeEarned));
    }

    private void validateBadge(Game game, PlayingStats playingStats, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        String name = playingStats.getPlayer().getName();
        int count = StringUtils.countMatches(playingStats.getSpecialsSequence(), word);
        if (count != 0) {
            long nextLevel = badgeOps.getProgress(badge, name) + count;
            updateBadgeLevel(game, name, badge, nextLevel, badgeOps, onBadgeEarned);
        }
    }

}
