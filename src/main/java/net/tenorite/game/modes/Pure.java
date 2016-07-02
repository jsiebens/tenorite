package net.tenorite.game.modes;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import org.springframework.stereotype.Component;

@Component
public final class Pure extends GameMode {

    public static final GameModeId ID = GameModeId.of("PURE");

    private static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
        );

    public Pure() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return "Pure";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return "no specials";
    }
    
}
