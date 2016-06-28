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

}
