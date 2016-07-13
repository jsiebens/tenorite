package net.tenorite.modes;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameListener;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameRules;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.tenorite.badges.BadgeValidators.*;
import static net.tenorite.game.SpecialOccurancy.specialOccurancy;

@Component
public final class GBomb extends GameMode {

    public static final GameModeId ID = GameModeId.of("GBOMB");

    private static final GameRules RULES = GameRules
        .gameRules(b -> b
            .classicRules(false)
            .linesPerSpecial(2)
            .specialCapacity(5)
            .specialOccurancy(
                specialOccurancy(o -> o
                    .blockBomb(85)
                    .gravity(15)
                )
            )
        );

    public GBomb() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return "GBomb";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return "only gravity and bombs";
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new SuddenDeath(240, 5, 1, scheduler, channel);
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
