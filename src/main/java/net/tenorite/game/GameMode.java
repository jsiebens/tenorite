package net.tenorite.game;

import net.tenorite.core.Tempo;

import java.util.function.Function;

import static net.tenorite.game.BlockOccurancy.blockOccurancy;
import static net.tenorite.game.GameRules.defaultGameRules;
import static net.tenorite.game.GameRules.gameRules;

public enum GameMode {

    DEFAULT(
        t -> t.equals(Tempo.FAST) ? "TetriFAST" : "TetriNET",
        defaultGameRules(),
        SuddenDeath.of(300, 10, 1)
    ),

    CLASSIC(
        t -> t.equals(Tempo.FAST) ? "classic TetriFAST" : "classic TetriNET",
        gameRules(b -> b
            .classicRules(true)
        ),
        SuddenDeath.of(300, 10, 1)
    ),

    PURE(
        t -> "no specials",
        gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
        ),
        SuddenDeath.of(300, 10, 1)
    ),

    SNS(
        t -> "sticks and squares, no specials",
        gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
            .blockOccurancy(
                blockOccurancy(o -> o
                    .line(50)
                    .square(50)
                )
            )
        ),
        SuddenDeath.of(300, 10, 1)
    ),

    JELLY(
        t -> "just J and L bricks, no specials",
        gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
            .blockOccurancy(
                blockOccurancy(o -> o
                    .leftL(50)
                    .rightL(50)
                )
            )
        ),
        SuddenDeath.of(300, 10, 1)
    );

    private final Function<Tempo, String> description;

    private final GameRules gameRules;

    private final SuddenDeath suddenDeath;

    GameMode(Function<Tempo, String> description, GameRules gameRules, SuddenDeath suddenDeath) {
        this.description = description;
        this.gameRules = gameRules;
        this.suddenDeath = suddenDeath;
    }

    public String getDescription(Tempo tempo) {
        return description.apply(tempo);
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public SuddenDeath getSuddenDeath() {
        return suddenDeath;
    }

}
