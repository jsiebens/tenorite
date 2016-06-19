package net.tenorite.game;

import net.tenorite.core.Block;
import net.tenorite.core.Special;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

@Value.Immutable
@ImmutableStyle
public abstract class GameRules {

    public static GameRules defaultGameRules() {
        return new GameRulesBuilder().build();
    }

    public static GameRules gameRules(Consumer<GameRulesBuilder> consumer) {
        GameRulesBuilder builder = new GameRulesBuilder();
        consumer.accept(builder);
        return builder.build();
    }

    public static GameRules from(GameRules gameRules, Consumer<GameRulesBuilder> consumer) {
        GameRulesBuilder builder = new GameRulesBuilder().from(gameRules);
        consumer.accept(builder);
        return builder.build();
    }

    @Value.Default
    public int getStartingHeight() {
        return 0;
    }

    @Value.Default
    public int getStartingLevel() {
        return 0;
    }

    @Value.Default
    public int getLinesPerLevel() {
        return 2;
    }

    @Value.Default
    public int getLevelIncrease() {
        return 1;
    }

    @Value.Default
    public int getLinesPerSpecial() {
        return 1;
    }

    @Value.Default
    public int getSpecialAdded() {
        return 1;
    }

    @Value.Default
    public int getSpecialCapacity() {
        return 18;
    }

    @Value.Default
    public boolean getAverageLevels() {
        return true;
    }

    @Value.Default
    public boolean getClassicRules() {
        return false;
    }

    @Value.Default
    public BlockOccurancy getBlockOccurancy() {
        return BlockOccurancy.defaultBlockOccurancy();
    }

    @Value.Default
    public SpecialOccurancy getSpecialOccurancy() {
        return SpecialOccurancy.defaultSpecialOccurancy();
    }

    @Override
    public String toString() {

        StringBuilder message = new StringBuilder();
        message.append(getStartingHeight());
        message.append(" ");
        message.append(getStartingLevel());
        message.append(" ");
        message.append(getLinesPerLevel());
        message.append(" ");
        message.append(getLevelIncrease());
        message.append(" ");
        message.append(getLinesPerSpecial());
        message.append(" ");
        message.append(getSpecialAdded());
        message.append(" ");
        message.append(getSpecialCapacity());
        message.append(" ");

        stream(Block.values()).forEach(b -> range(0, getBlockOccurancy().occurancyOf(b)).forEach(i -> message.append(b.getNumber())));
        message.append(" ");
        stream(Special.values()).forEach(s -> range(0, getSpecialOccurancy().occurancyOf(s)).forEach(i -> message.append(s.getNumber())));
        message.append(" ");

        message.append(getAverageLevels() ? "1" : "0");
        message.append(" ");
        message.append(getClassicRules() ? "1" : "0");

        return message.toString();
    }

}
