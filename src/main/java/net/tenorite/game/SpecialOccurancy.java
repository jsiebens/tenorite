package net.tenorite.game;

import net.tenorite.core.Special;
import net.tenorite.util.ImmutableStyle;
import net.tenorite.util.Occurancy;
import org.immutables.value.Value;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Value.Immutable
@ImmutableStyle
public abstract class SpecialOccurancy {

    public static SpecialOccurancy specialOccurancy(Consumer<SpecialOccurancyBuilder> consumer) {
        SpecialOccurancyBuilder builder = new SpecialOccurancyBuilder();
        consumer.accept(builder);
        return builder.build().normalize();
    }

    public static SpecialOccurancy defaultSpecialOccurancy() {
        return specialOccurancy(
            b -> b
                .addLine(22)
                .clearLine(18)
                .nukeField(1)
                .randomClear(16)
                .switchField(1)
                .clearSpecial(15)
                .gravity(5)
                .quakeField(12)
                .blockBomb(10)
        );
    }

    @Value.Default
    public int getAddLine() {
        return 0;
    }

    @Value.Default
    public int getClearLine() {
        return 0;
    }

    @Value.Default
    public int getNukeField() {
        return 0;
    }

    @Value.Default
    public int getRandomClear() {
        return 0;
    }

    @Value.Default
    public int getSwitchField() {
        return 0;
    }

    @Value.Default
    public int getClearSpecial() {
        return 0;
    }

    @Value.Default
    public int getGravity() {
        return 0;
    }

    @Value.Default
    public int getQuakeField() {
        return 0;
    }

    @Value.Default
    public int getBlockBomb() {
        return 0;
    }

    public final SpecialOccurancy normalize() {
        Map<Special, Integer> occ = stream(Special.values()).collect(toMap(identity(), this::occurancyOf));
        Map<Special, Integer> normalized = Occurancy.normalize(Special.class, occ);

        return new SpecialOccurancyBuilder()
            .addLine(normalized.get(Special.ADDLINE))
            .clearLine(normalized.get(Special.CLEARLINE))
            .nukeField(normalized.get(Special.NUKEFIELD))
            .randomClear(normalized.get(Special.RANDOMCLEAR))
            .switchField(normalized.get(Special.SWITCHFIELD))
            .clearSpecial(normalized.get(Special.CLEARSPECIAL))
            .gravity(normalized.get(Special.GRAVITY))
            .quakeField(normalized.get(Special.QUAKEFIELD))
            .blockBomb(normalized.get(Special.BLOCKBOMB))
            .build();
    }

    public final int occurancyOf(Special special) {
        switch (special) {
            case ADDLINE:
                return getAddLine();
            case CLEARLINE:
                return getClearLine();
            case NUKEFIELD:
                return getNukeField();
            case RANDOMCLEAR:
                return getRandomClear();
            case SWITCHFIELD:
                return getSwitchField();
            case CLEARSPECIAL:
                return getClearSpecial();
            case GRAVITY:
                return getGravity();
            case QUAKEFIELD:
                return getQuakeField();
            case BLOCKBOMB:
                return getBlockBomb();
            default:
                throw new IllegalStateException("unmapped special");
        }
    }

}