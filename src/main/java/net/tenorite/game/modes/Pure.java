package net.tenorite.game.modes;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.validators.NrOfConsecutiveGamesLost;
import net.tenorite.badges.validators.NrOfConsecutiveGamesWon;
import net.tenorite.badges.validators.NrOfGamesPlayed;
import net.tenorite.badges.validators.NrOfGamesWon;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            new NrOfGamesPlayed(Badge.of(ID, "COMPETITOR"), 10),
            new NrOfGamesWon(Badge.of(ID, "LIKE_A_PRO"), 10),
            new NrOfGamesWon(Badge.of(ID, "LIKE_A_KING"), 1000),
            new NrOfConsecutiveGamesWon(Badge.of(ID, "I_M_ON_FIRE"), 5),
            new NrOfConsecutiveGamesLost(Badge.of(ID, "JUST_KEEP_TRYING"), 5)
        );
    }
    
}
