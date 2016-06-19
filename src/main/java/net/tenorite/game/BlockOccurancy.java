package net.tenorite.game;

import net.tenorite.core.Block;
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
public abstract class BlockOccurancy {

    public static BlockOccurancy blockOccurancy(Consumer<BlockOccurancyBuilder> consumer) {
        BlockOccurancyBuilder builder = new BlockOccurancyBuilder();
        consumer.accept(builder);
        return builder.build().normalize();
    }

    public static BlockOccurancy defaultBlockOccurancy() {
        return blockOccurancy(
            b -> b
                .line(15)
                .square(15)
                .rightL(14)
                .rightZ(14)
                .leftL(14)
                .leftZ(14)
                .halfCross(14)
        );
    }

    @Value.Default
    public int getLine() {
        return 0;
    }

    @Value.Default
    public int getSquare() {
        return 0;
    }

    @Value.Default
    public int getLeftL() {
        return 0;
    }

    @Value.Default
    public int getRightL() {
        return 0;
    }

    @Value.Default
    public int getLeftZ() {
        return 0;
    }

    @Value.Default
    public int getRightZ() {
        return 0;
    }

    @Value.Default
    public int getHalfCross() {
        return 0;
    }

    public final BlockOccurancy normalize() {
        Map<Block, Integer> occ = stream(Block.values()).collect(toMap(identity(), this::occurancyOf));
        Map<Block, Integer> normalized = Occurancy.normalize(Block.class, occ);

        return
            new BlockOccurancyBuilder()
                .line(normalized.get(Block.LINE))
                .square(normalized.get(Block.SQUARE))
                .rightL(normalized.get(Block.RIGHTL))
                .rightZ(normalized.get(Block.RIGHTZ))
                .leftL(normalized.get(Block.LEFTL))
                .leftZ(normalized.get(Block.LEFTZ))
                .halfCross(normalized.get(Block.HALFCROSS))
                .build();
    }

    public final int occurancyOf(Block block) {
        switch (block) {
            case LINE:
                return getLine();
            case SQUARE:
                return getSquare();
            case RIGHTL:
                return getRightL();
            case RIGHTZ:
                return getRightZ();
            case LEFTL:
                return getLeftL();
            case LEFTZ:
                return getLeftZ();
            case HALFCROSS:
                return getHalfCross();
            default:
                throw new IllegalStateException("unmapped block");
        }
    }

}