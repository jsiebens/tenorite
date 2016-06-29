package net.tenorite.game.modes;

import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import org.springframework.stereotype.Component;

import static net.tenorite.game.BlockOccurancy.blockOccurancy;

@Component
public final class Jelly extends GameMode {

    public static final GameModeId ID = GameModeId.of("JELLY");

    public static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
            .blockOccurancy(
                blockOccurancy(o -> o
                    .leftL(50)
                    .rightL(50)
                )
            )
        );

    public Jelly() {
        super(ID, t -> "Jelly", RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
