package net.tenorite.net;

import java.util.Optional;

final class InitTokenDecoder {

    public static final String TETRISSTART = "tetrisstart";

    public static final String TETRIFASTER = "tetrifaster";

    public static Optional<String> decode(String initString) {
        // check the size of the init string
        if (initString.length() % 2 != 0) {
            return Optional.empty();
        }

        // parse the hex values from the init string
        int[] dec = new int[initString.length() / 2];

        try {
            for (int i = 0; i < dec.length; i++) {
                dec[i] = Integer.parseInt(initString.substring(i * 2, i * 2 + 2), 16);
            }
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }

        // find the hash pattern for a tetrinet client
        String pattern = findHashPattern(dec, false);

        // find the hash pattern for a tetrifast client
        if (pattern.length() == 0) {
            pattern = findHashPattern(dec, true);
        }

        // check the size of the pattern found
        if (pattern.length() == 0) {
            return Optional.empty();
        }

        // decode the string
        StringBuilder s = new StringBuilder();

        for (int i = 1; i < dec.length; i++) {
            s.append((char) (((dec[i] ^ pattern.charAt((i - 1) % pattern.length())) + 255 - dec[i - 1]) % 255));
        }

        return Optional.of(s.toString().replace((char) 0, (char) 255));
    }

    private static String findHashPattern(int[] dec, boolean tetrifast) {
        // the first characters from the decoded string
        char[] data = (tetrifast ? TETRIFASTER : TETRISSTART).substring(0, 10).toCharArray();

        // compute the full hash
        int[] hash = new int[data.length];

        for (int i = 0; i < data.length; i++) {
            hash[i] = ((data[i] + dec[i]) % 255) ^ dec[i + 1];
        }

        // find the length of the hash
        int length = 5;

        for (int i = 5; i == length && i > 0; i--) {
            for (int j = 0; j < data.length - length; j++) {
                if (hash[j] != hash[j + length]) {
                    length--;
                }
            }
        }

        return new String(hash, 0, length);
    }


}
