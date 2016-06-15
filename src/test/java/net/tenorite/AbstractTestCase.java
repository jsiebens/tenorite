package net.tenorite;

public abstract class AbstractTestCase {

    public static final byte[] IP = {(byte) 195, (byte) 139, (byte) 204, (byte) 206};

    protected static String encode(String type, String nickname, String version) {
        int p = 54 * (IP[0] & 0xFF) + 41 * (IP[1] & 0xFF) + 29 * (IP[2] & 0xFF) + 17 * (IP[3] & 0xFF);
        char[] pattern = String.valueOf(p).toCharArray();
        char[] data = (type + " " + nickname + " " + version).toCharArray();

        StringBuilder result = new StringBuilder();
        char offset = 0x80;
        result.append(toHex(offset));

        char previous = offset;

        for (int i = 0; i < data.length; i++) {
            char current = (char) (((previous + data[i]) % 255) ^ pattern[i % pattern.length]);
            result.append(toHex(current));
            previous = current;
        }

        return result.toString().toUpperCase();
    }

    private static String toHex(char c) {
        String h = Integer.toHexString(c);
        return h.length() > 1 ? h : "0" + h;
    }

}
