package net.tenorite.game.modes;

import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;

import static net.tenorite.game.BlockOccurancy.blockOccurancy;

public final class SticksAndSquares extends GameMode {

    public static final GameModeId ID = GameModeId.of("SNS");

    public static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
            .blockOccurancy(
                blockOccurancy(o -> o
                    .line(50)
                    .square(50)
                )
            )
        );

    public SticksAndSquares() {
        super(ID, RULES, (s, c) -> new SuddenDeath(300, 10, 1, s, c));
    }

}
