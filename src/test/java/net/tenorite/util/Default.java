package net.tenorite.util;

import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;

public final class Default extends GameMode {

    public static final GameModeId ID = GameModeId.of("DEFAULT");

    private static final GameRules RULES = GameRules.defaultGameRules();

    public Default() {
        super(ID, RULES);
    }

}
