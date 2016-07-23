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
package net.tenorite.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Johan Siebens
 */
public final class Style {

    private static Map<String, String> STYLES = new HashMap<>();

    static {
        STYLES.put("red", "\u0014");
        STYLES.put("black", "\u0004");
        STYLES.put("green", "\u000c");
        STYLES.put("lightGreen", "\u000e");
        STYLES.put("darkBlue", "\u0011");
        STYLES.put("blue", "\u0005");
        STYLES.put("cyan", "\u0003");
        STYLES.put("aqua", "\u0017");
        STYLES.put("yellow", "\u0019");
        STYLES.put("kaki", "\u0012");
        STYLES.put("brown", "\u0010");
        STYLES.put("lightGray", "\u000f");
        STYLES.put("gray", "\u0006");
        STYLES.put("magenta", "\u0008");
        STYLES.put("purple", "\u0013");
        STYLES.put("b", "\u0002");
        STYLES.put("i", "\u0016");
        STYLES.put("u", "\u001f");
        STYLES.put("white", "\u0018");
    }

    public static String apply(String text) {
        for (String key : STYLES.keySet()) {
            String value = STYLES.getOrDefault(key, "");
            text = text.replaceAll("<" + key + ">", value);
            text = text.replaceAll("</" + key + ">", value);
        }

        return text;
    }

}
