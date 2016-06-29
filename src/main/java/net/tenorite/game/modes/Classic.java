package net.tenorite.game.modes;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import org.springframework.stereotype.Component;

@Component
public final class Classic extends GameMode {

    public static final GameModeId ID = GameModeId.of("CLASSIC");

    public static final GameRules RULES = GameRules.gameRules(b -> b.classicRules(true));

    public Classic() {
        super(ID, t -> t.equals(Tempo.FAST) ? "Classic TetriFAST" : "Classic TetriNET", RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
