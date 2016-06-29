package net.tenorite.game;

import net.tenorite.core.Special;

import java.util.List;

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

        };
    }

}
