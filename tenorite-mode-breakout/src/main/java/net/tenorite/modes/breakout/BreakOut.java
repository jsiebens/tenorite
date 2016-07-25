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
package net.tenorite.modes.breakout;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerWonMessage;
import net.tenorite.protocol.SpecialBlockMessage;
import net.tenorite.util.Scheduler;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static net.tenorite.badges.validators.BadgeValidators.*;
import static net.tenorite.game.GameRules.gameRules;
import static org.apache.commons.lang3.RandomUtils.nextInt;

/**
 * @author Johan Siebens
 */
public final class BreakOut extends GameMode {

    public static final GameModeId ID = GameModeId.of("BREAK_OUT");

    private static final GameRules RULES = gameRules(b -> b
        .classicRules(false)
        .specialAdded(0)
        .specialCapacity(1)
    );

    public BreakOut() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return "Break out!";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return "use the Nuke to win!";
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new Listener(channel).and(new SuddenDeath(240, 5, 1, scheduler, channel));
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),
            fastAndFurious(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID)
        );
    }

    private static String createStartField() {
        return
            "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                "000000000000" +
                garbageLine() +
                garbageLine() +
                garbageLine() +
                "n0000nn0000n" +
                "000000000000";
    }

    private static String garbageLine() {
        StringBuilder line = new StringBuilder(range(0, Field.WIDTH).mapToObj(i -> String.valueOf(nextInt(0, 6))).collect(joining()));
        line.setCharAt(nextInt(0, Field.WIDTH), '0');
        return line.toString();
    }

    private static class Listener implements GameListener {

        private final List<Player> players = new ArrayList<>();

        private final Set<Integer> slots = new HashSet<>();

        private final Consumer<Message> channel;

        private String startField;

        private Listener(Consumer<Message> channel) {
            this.channel = channel;
        }

        @Override
        public void onStartGame(List<Player> players) {
            this.startField = createStartField();
            this.players.addAll(players);
        }

        @Override
        public void onFieldUpdate(Player sender, Field field) {
            if (slots.add(sender.getSlot())) {
                channel.accept(FieldMessage.of(sender.getSlot(), startField, true));
            }
        }

        @Override
        public void onClassicStyleAdd(Player sender, int lines) {
            if (lines == 4) {
                players.stream().filter(s -> !sender.equals(s)).forEach(s -> channel.accept(SpecialBlockMessage.of(sender.getSlot(), Special.QUAKEFIELD, s.getSlot(), true)));
            }
        }

        @Override
        public void onSpecial(Player sender, Special special, Player target) {
            if (special.equals(Special.NUKEFIELD)) {
                channel.accept(PlayerWonMessage.of(sender.getSlot()));
            }
        }

    }

}
