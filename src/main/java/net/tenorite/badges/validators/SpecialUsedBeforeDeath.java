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
package net.tenorite.badges.validators;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Special;
import net.tenorite.game.*;
import net.tenorite.game.events.GameFinished;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.SpecialBlockMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/**
 * @author Johan Siebens
 */
public final class SpecialUsedBeforeDeath extends BadgeValidator {

    private static final int TARGET = 20;

    private final Set<Special> specials;

    public SpecialUsedBeforeDeath(Badge badge, Set<Special> specials) {
        super(badge);
        this.specials = new HashSet<>(specials);
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        Map<Integer, Player> allPlayers = gameFinished.getRanking().stream().map(PlayingStats::getPlayer).collect(Collectors.toMap(Player::getSlot, identity()));

        Game game = gameFinished.getGame();

        Map<Player, Integer> counts = new HashMap<>();
        Map<Integer, Integer> currentHeights = new HashMap<>();

        for (GameMessage gameMessage : game.getMessages()) {
            Message message = gameMessage.getMessage();

            if (message instanceof FieldMessage) {
                FieldMessage fieldMessage = (FieldMessage) message;
                int height = Field.of(fieldMessage.getUpdate()).getHighest();
                currentHeights.put(fieldMessage.getSender(), height);
            }

            if (message instanceof SpecialBlockMessage) {
                SpecialBlockMessage sb = (SpecialBlockMessage) message;
                if ((sb.getSender() == sb.getTarget()) && checkSpecial(sb) && currentHeights.getOrDefault(sb.getTarget(), 0) >= TARGET) {
                    ofNullable(allPlayers.get(sb.getSender())).ifPresent(player -> counts.compute(player, (p, x) -> x == null ? 1 : x + 1));
                }
            }
        }

        counts.entrySet().forEach(e -> {
            Player p = e.getKey();
            long nextLevel = badgeOps.getProgress(badge, p.getName()) + e.getValue();
            updateBadgeLevel(game, p.getName(), badge, nextLevel, badgeOps, onBadgeEarned);
        });
    }

    private boolean checkSpecial(SpecialBlockMessage sb) {
        return specials.contains(sb.getSpecial());
    }

}
