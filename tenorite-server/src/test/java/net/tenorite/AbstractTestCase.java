/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite;

/**
 * @author Johan Siebens
 */
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
