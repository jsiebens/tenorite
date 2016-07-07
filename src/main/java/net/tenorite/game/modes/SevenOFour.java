package net.tenorite.game.modes;

import net.tenorite.badges.BadgeValidator;
import net.tenorite.core.Tempo;
import net.tenorite.game.*;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerWonMessage;
import net.tenorite.util.Scheduler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

import static net.tenorite.badges.BadgeValidators.*;
import static net.tenorite.game.PlayingStats.*;

@Component
public final class SevenOFour extends GameMode {

    private static final Comparator<PlayingStats> BY_FOUR_LINE_COMBOS = (o1, o2) -> o1.getNrOfFourLineCombos() - o2.getNrOfFourLineCombos();

    private static final Comparator<PlayingStats> COMPARATOR =
        BY_FOUR_LINE_COMBOS.reversed() // most four line combos first
            .thenComparing(BY_COMBOS.reversed()) // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    public static final GameModeId ID = GameModeId.of("SOF");

    private static final GameRules RULES = GameRules.gameRules(b -> b
        .classicRules(false)
        .specialAdded(0)
        .specialCapacity(0)
    );

    public SevenOFour() {
        super(ID, RULES);
    }

    @Override
    public String getTitle(Tempo tempo) {
        return "Seven 'o Four";
    }

    @Override
    public String getDescription(Tempo tempo) {
        return super.getDescription(tempo);
    }

    @Override
    public GameListener createGameListener(Scheduler scheduler, Consumer<Message> channel) {
        return new Listener(channel).and(new SuddenDeath(300, 10, 1, scheduler, channel));
    }

    @Override
    public Comparator<PlayingStats> getPlayingStatsComparator() {
        return COMPARATOR;
    }

    @Override
    public List<BadgeValidator> getBadgeValidators() {
        return Arrays.asList(
            competitor(ID),
            likeAPro(ID),
            likeAKing(ID),
            imOnFire(ID),
            justKeepTrying(ID),

            eliminator(ID),
            eradicator(ID),
            dropsInTheBucket(ID),
            dropItLikeItsHot(ID)
        );
    }

    private static final class Listener implements GameListener {

        private static final int TARGET = 7;

        private final Consumer<Message> channel;

        private Map<Integer, Integer> nrOfFourLines = new HashMap<>();

        Listener(Consumer<Message> channel) {
            this.channel = channel;
        }

        @Override
        public void onClassicStyleAdd(Player sender, int lines) {
            if (lines == 4 && incr(sender.getSlot()) >= TARGET) {
                channel.accept(PlayerWonMessage.of(sender.getSlot()));
            }
        }

        private int incr(int sender) {
            return nrOfFourLines.compute(sender, (k, v) -> v == null ? 1 : v + 1);
        }

    }

}
