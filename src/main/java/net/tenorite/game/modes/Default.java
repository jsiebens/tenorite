package net.tenorite.game.modes;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import org.springframework.stereotype.Component;

public final class Default extends GameMode {

    public static final GameModeId ID = GameModeId.of("DEFAULT");

    public static final GameRules RULES = GameRules.defaultGameRules();

    public Default() {
        super(ID, t -> t.equals(Tempo.FAST) ? "TetriFAST" : "TetriNET", RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
