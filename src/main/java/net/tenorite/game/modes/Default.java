package net.tenorite.game.modes;

import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;

public final class Default extends GameMode {

    public static final GameModeId ID = GameModeId.of("DEFAULT");

    public static final GameRules RULES = GameRules.defaultGameRules();

    public Default() {
        super(ID, RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
