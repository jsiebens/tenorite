package net.tenorite.game.modes;

import net.tenorite.game.*;
import net.tenorite.game.listeners.SuddenDeath;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerWonMessage;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.tenorite.game.PlayingStats.*;

@Component
public final class SevenOFour extends GameMode {

    public static final Comparator<PlayingStats> BY_FOUR_LINE_COMBOS = (o1, o2) -> o1.getNrOfFourLineCombos() - o2.getNrOfFourLineCombos();

    private static final Comparator<PlayingStats> COMPARATOR =
        BY_FOUR_LINE_COMBOS.reversed() // most four line combos first
            .thenComparing(BY_COMBOS.reversed()) // most combos first
            .thenComparing(BY_LEVEL.reversed()) // highest levels first
            .thenComparing(BY_BLOCKS.reversed()) // most blocks first
            .thenComparing(BY_MAX_HEIGTH); // lowest field first

    public static final GameModeId ID = GameModeId.of("7o4");

    public static final GameRules RULES = GameRules.gameRules(b -> b
        .classicRules(false)
        .specialAdded(0)
        .specialCapacity(0)
    );

    public SevenOFour() {
        super(ID, t -> "Seven 'o Four", RULES, (s, c) -> new Listener(c).and(new SuddenDeath(300, 10, 1, s, c)), COMPARATOR);
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
