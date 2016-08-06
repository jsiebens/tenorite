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
package net.tenorite.game;

import net.tenorite.core.Special;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameListenerTest {

    @Test
    public void testChainOnStartAndEndGame() {
        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onStartGame(List<Player> players) {
                sequence.append("SA");
            }

            @Override
            public void onEndGame() {
                sequence.append("EA");
            }
        };

        GameListener b = new GameListener() {

            @Override
            public void onStartGame(List<Player> players) {
                sequence.append("SB");
            }

            @Override
            public void onEndGame() {
                sequence.append("EB");
            }
        };

        a.and(b).onStartGame(emptyList());
        a.and(b).onEndGame();

        assertThat(sequence.toString()).isEqualTo("SASBEAEB");
    }

    @Test
    public void testChainOnPauseAndOnResume() {
        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onPauseGame() {
                sequence.append("PA");
            }

            @Override
            public void onResumeGame() {
                sequence.append("RA");
            }

        };

        GameListener b = new GameListener() {

            @Override
            public void onPauseGame() {
                sequence.append("PB");
            }

            @Override
            public void onResumeGame() {
                sequence.append("RB");
            }

        };

        a.and(b).onPauseGame();
        a.and(b).onResumeGame();

        assertThat(sequence.toString()).isEqualTo("PAPBRARB");
    }

    @Test
    public void testChainOnSpecial() {
        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onSpecial(Player sender, Special special, Player target) {
                sequence.append("A")
                    .append(sender.getSlot())
                    .append(special.getLetter())
                    .append(target.getSlot());
            }

        };

        GameListener b = new GameListener() {

            @Override
            public void onSpecial(Player sender, Special special, Player target) {
                sequence.append("B")
                    .append(sender.getSlot())
                    .append(special.getLetter())
                    .append(target.getSlot());
            }

        };

        a.and(b).onSpecial(Player.of(1, "john", ""), Special.QUAKEFIELD, Player.of(2, "jane", ""));

        assertThat(sequence.toString()).isEqualTo("A1q2B1q2");
    }

    @Test
    public void testChainClassic() {
        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onClassicStyleAdd(Player sender, int lines) {
                sequence.append("A")
                    .append(sender.getSlot())
                    .append(lines);
            }

        };

        GameListener b = new GameListener() {

            @Override
            public void onClassicStyleAdd(Player sender, int lines) {
                sequence.append("B")
                    .append(sender.getSlot())
                    .append(lines);
            }

        };

        a.and(b).onClassicStyleAdd(Player.of(1, "john", ""), 4);

        assertThat(sequence.toString()).isEqualTo("A14B14");
    }

    @Test
    public void testChainField() {
        Field field = Field.randomCompletedField();

        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onFieldUpdate(Player sender, Field field) {
                sequence.append("A")
                    .append(sender.getSlot())
                    .append(field.getFieldString());
            }

        };

        GameListener b = new GameListener() {

            @Override
            public void onFieldUpdate(Player sender, Field field) {
                sequence.append("B")
                    .append(sender.getSlot())
                    .append(field.getFieldString());
            }

        };

        a.and(b).onFieldUpdate(Player.of(1, "john", ""), field);

        assertThat(sequence.toString()).isEqualTo("A1" + field.getFieldString() + "B1" + field.getFieldString());
    }

    @Test
    public void testChainOnLevelUpdate() {
        StringBuilder sequence = new StringBuilder();

        GameListener a = new GameListener() {

            @Override
            public void onLevelUpdate(Player sender, int level) {
                sequence.append("A");
            }

        };

        GameListener b = new GameListener() {

            @Override
            public void onLevelUpdate(Player sender, int level) {
                sequence.append("B");
            }

        };

        a.and(b).onLevelUpdate(Player.of(1, "john", ""), 5);

        assertThat(sequence.toString()).isEqualTo("AB");
    }

}
