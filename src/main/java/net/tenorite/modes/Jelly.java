package net.tenorite.modes;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static net.tenorite.badges.BadgeValidators.*;
import static net.tenorite.game.BlockOccurancy.blockOccurancy;

/**
 * @author Johan Siebens
 */
@Component
public final class Jelly extends GameMode {

    public static final GameModeId ID = GameModeId.of("JELLY");

    private static final GameRules RULES = GameRules
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
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return "Jelly";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return "just J and L bricks, no specials";
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),
            fastAndFurious(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID)
        );
    }

}
