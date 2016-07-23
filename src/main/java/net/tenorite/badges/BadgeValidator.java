/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.badges;

import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Tempo;
import net.tenorite.game.Game;
import net.tenorite.game.events.GameFinished;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Johan Siebens
 */
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

    protected static void updateBadgeLevelAndProgressWhenTargetIsReached(Game game, String name, Badge badge, long count, int target, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        if (count >= target) {
            Optional<BadgeLevel> opt = badgeOps.getBadgeLevel(name, badge);
            long newLevel = (count / target) + opt.map(BadgeLevel::getLevel).orElse(0L);

            BadgeLevel badgeLevel = BadgeLevel.of(game.getTempo(), badge, name, game.getTimestamp(), newLevel, game.getId());
            badgeOps.saveBadgeLevel(badgeLevel);
            onBadgeEarned.accept(BadgeEarned.of(badgeLevel, opt.isPresent()));

            badgeOps.updateProgress(badge, name, count % target);
        }
        else {
            badgeOps.updateProgress(badge, name, count);
        }
    }

    protected static void updateBadgeLevel(Game game, String name, Badge badge, long count, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        if (count != 0) {
            Optional<BadgeLevel> optBadgeLevel = badgeOps.getBadgeLevel(name, badge);
            long currentLevel = optBadgeLevel.isPresent() ? optBadgeLevel.get().getLevel() : 0;
            if (count > currentLevel) {
                BadgeLevel badgeLevel = BadgeLevel.of(game.getTempo(), badge, name, game.getTimestamp(), count, game.getId());

                badgeOps.saveBadgeLevel(badgeLevel);
                badgeOps.updateProgress(badge, name, count);

                onBadgeEarned.accept(BadgeEarned.of(badgeLevel, optBadgeLevel.isPresent()));
            }
        }
    }

}
