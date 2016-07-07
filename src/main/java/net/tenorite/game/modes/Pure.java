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

import static net.tenorite.badges.BadgeValidators.*;
import static net.tenorite.badges.BadgeValidators.justKeepTrying;

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
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),

            comboWombo(ID),
            doubleTrouble(ID),
            threeIsCompany(ID),
            fourOClock(ID),

            comboStrike(ID),
            doubleStrike(ID),
            tripleStrike(ID),
            quadrupleStrike(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID)
        );
    }

}
