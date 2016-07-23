package net.tenorite.game;

import net.tenorite.core.Special;

import java.util.StringTokenizer;
import java.util.function.Supplier;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomUtils.nextInt;

/**
 * @author Johan Siebens
 */
public final class Field {

    public static final int WIDTH = 12;

    public static final int HEIGHT = 22;

    private static final char EMPTY = '0';

    public static Field of(String fieldString) {
        return new Field(readFieldString(fieldString));
    }

    public static Field empty() {
        return new Field(readFieldString(EMPTY_FIELDSTRING));
    }

    public static Field randomCompletedField() {
        return new Field(readFieldString(RANDOM_FILLED_FIELDSTRING.get()));
    }

    private static final char[] BLOCKS = {
        EMPTY, '1', '2', '3', '4', '5',
        Special.ADDLINE.getLetter(),
        Special.CLEARLINE.getLetter(),
        Special.NUKEFIELD.getLetter(),
        Special.RANDOMCLEAR.getLetter(),
        Special.SWITCHFIELD.getLetter(),
        Special.CLEARSPECIAL.getLetter(),
        Special.GRAVITY.getLetter(),
        Special.QUAKEFIELD.getLetter(),
        Special.BLOCKBOMB.getLetter()};

    private char[][] field = new char[WIDTH][HEIGHT];

    private Field(char[][] field) {
        this.field = field;
    }

    public Field update(String fieldString) {
        if (fieldString == null || fieldString.trim().isEmpty()) {
            return this;
        }

        if (isFullUpdate(fieldString)) {
            return new Field(readFieldString(fieldString));
        }
        else {
            char[][] newField = readFieldString(this.getFieldString());

            StringTokenizer tokenizer = new StringTokenizer(fieldString, "!\"#$%&'()*+,-./", true);

            while (tokenizer.hasMoreTokens()) {
                // block type
                String type = tokenizer.nextToken();
                char color = BLOCKS[type.charAt(0) - 0x21];

                // locations
                String locations = tokenizer.nextToken();
                for (int i = 0; i < locations.length(); i = i + 2) {
                    int x = locations.charAt(i) - '3';
                    int y = HEIGHT - (locations.charAt(i + 1) - '3') - 1;
                    newField[x][y] = color;
                }
            }

            return new Field(newField);
        }
    }

    public String getFieldString() {
        char[] buffer = new char[WIDTH * HEIGHT];
        int k = 0;
        for (int j = HEIGHT - 1; j >= 0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                buffer[k++] = field[i][j];
            }
        }
        return new String(buffer);
    }

    public int getHighest() {
        for (int i = HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < WIDTH; j++) {
                if (field[j][i] != EMPTY) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    public int getNrOfSpecials() {
        int count = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < WIDTH; j++) {
                if (Character.isLetter(field[j][i])) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getNrOfBlocks(Special special) {
        char target = special.getLetter();
        int count = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < WIDTH; j++) {
                if (field[j][i] == target) {
                    count++;
                }
            }
        }
        return count;
    }

    char getBlock(int x, int y) {
        return field[x][y];
    }

    private static char[][] readFieldString(String fieldString) {
        char[][] blocks = new char[WIDTH][HEIGHT];
        for (int i = 0; i < fieldString.length(); i++) {
            char c = fieldString.charAt(i);
            blocks[i % WIDTH][HEIGHT - i / WIDTH - 1] = c;
        }
        return blocks;
    }

    private static boolean isFullUpdate(String update) {
        return update.length() == Field.WIDTH * Field.HEIGHT;
    }

    private static final String EMPTY_FIELDSTRING = range(0, HEIGHT * WIDTH).mapToObj(i -> valueOf(EMPTY)).collect(joining());

    private static final Supplier<String> RANDOM_FILLED_FIELDSTRING = () -> range(0, HEIGHT * WIDTH).mapToObj(i -> valueOf(nextInt(1, 6))).collect(joining());

}
