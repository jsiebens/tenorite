package net.tenorite.util;

/**
 * @author Johan Siebens
 */
public final class Base36 {

    private static final String SEED = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String convert(long value) {
        int base = SEED.length();
        final StringBuilder sb = new StringBuilder(1);
        do {
            sb.insert(0, SEED.charAt((int) (value % base)));
            value /= base;
        } while (value > 0);
        return sb.toString();
    }

}
