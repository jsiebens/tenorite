package net.tenorite.game;

import net.tenorite.core.Tempo;

import java.util.function.Function;

import static net.tenorite.game.GameRules.defaultGameRules;
import static net.tenorite.game.GameRules.gameRules;

public enum GameMode {

    DEFAULT(
        t -> t.equals(Tempo.FAST) ? "TetriFAST" : "TetriNET",
        defaultGameRules()
    ),

    CLASSIC(
        t -> t.equals(Tempo.FAST) ? "classic TetriFAST" : "classic TetriNET",
        gameRules(b -> b
            .classicRules(true)
        )
    ),

    PURE(
        t -> "no specials",
        gameRules(b -> b
            .classicRules(true)
            .specialAdded(0)
            .specialCapacity(0)
        )
    );

    private final Function<Tempo, String> description;

    private final GameRules gameRules;

    GameMode(Function<Tempo, String> description, GameRules gameRules) {
        this.description = description;
        this.gameRules = gameRules;
    }

    public String getDescription(Tempo tempo) {
        return description.apply(tempo);
    }

    public GameRules getGameRules() {
        return gameRules;
    }

}
