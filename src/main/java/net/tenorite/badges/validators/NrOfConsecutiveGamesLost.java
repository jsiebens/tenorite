package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeLevel;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.game.Game;
import net.tenorite.game.PlayingStats;
import net.tenorite.game.events.GameFinished;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class NrOfConsecutiveGamesLost extends BadgeValidator {

    private final int target;

    public NrOfConsecutiveGamesLost(Badge type, int target) {
        super(type);
        this.target = target;
    }

    @Override
    protected void doProcess(GameFinished ge, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        Game game = ge.getGame();

        List<PlayingStats> reverseRanking = reverseRanking(ge.getRanking());
        PlayingStats loser = reverseRanking.get(0);

        String name = loser.getPlayer().getName();

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

        reverseRanking.stream().skip(1).forEach(p -> badgeOps.updateProgress(type, p.getPlayer().getName(), 0));
        
    }

    private List<PlayingStats> reverseRanking(List<PlayingStats> stats) {
        ArrayList<PlayingStats> reversed = new ArrayList<>(stats);
        Collections.reverse(reversed);
        return reversed;
    }

}
