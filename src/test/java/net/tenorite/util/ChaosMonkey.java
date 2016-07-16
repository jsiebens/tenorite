package net.tenorite.util;

import net.tenorite.core.Special;
import net.tenorite.game.*;
import net.tenorite.protocol.Message;

import java.util.function.Consumer;

public final class ChaosMonkey extends GameMode {

    public static final GameModeId ID = GameModeId.of("CHAOS");

    private static final GameRules RULES = GameRules.defaultGameRules();

    public ChaosMonkey() {
        super(ID, RULES);
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new GameListener() {

            @Override
            public void onSpecial(Player sender, Special special, Player target) {
                throw new IllegalStateException("Chaos Monkey!");
            }

        };

    }

}
