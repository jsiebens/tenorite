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

import java.util.List;

/**
 * @author Johan Siebens
 */
public interface GameListener {

    GameListener NOOP = new GameListener() {

    };

    default void onStartGame(List<Player> players) {
    }

    default void onPauseGame() {
    }

    default void onResumeGame() {
    }

    default void onEndGame() {
    }

    default void onSpecial(Player sender, Special special, Player target) {
    }

    default void onClassicStyleAdd(Player sender, int lines) {
    }

    default void onFieldUpdate(Player sender, Field field) {
    }

    default void onLevelUpdate(Player sender, int level) {

    }

    default GameListener and(GameListener next) {
        GameListener me = this;
        return new GameListener() {

            @Override
            public void onStartGame(List<Player> players) {
                me.onStartGame(players);
                next.onStartGame(players);
            }

            @Override
            public void onPauseGame() {
                me.onPauseGame();
                next.onPauseGame();
            }

            @Override
            public void onResumeGame() {
                me.onResumeGame();
                next.onResumeGame();
            }

            @Override
            public void onEndGame() {
                me.onEndGame();
                next.onEndGame();
            }

            @Override
            public void onSpecial(Player sender, Special special, Player target) {
                me.onSpecial(sender, special, target);
                next.onSpecial(sender, special, target);
            }

            @Override
            public void onClassicStyleAdd(Player sender, int lines) {
                me.onClassicStyleAdd(sender, lines);
                next.onClassicStyleAdd(sender, lines);
            }

            @Override
            public void onFieldUpdate(Player sender, Field field) {
                me.onFieldUpdate(sender, field);
                next.onFieldUpdate(sender, field);
            }

            @Override
            public void onLevelUpdate(Player sender, int level) {
                me.onLevelUpdate(sender, level);
                next.onLevelUpdate(sender, level);
            }

        };
    }

}
