package net.tenorite.game.modes;

import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import org.springframework.stereotype.Component;

@Component
public final class Pure extends GameMode {

    public static final GameModeId ID = GameModeId.of("PURE");

    public static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
        );

    public Pure() {
        super(ID, t -> "Pure", RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
