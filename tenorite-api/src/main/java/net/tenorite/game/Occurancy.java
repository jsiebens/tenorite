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
package net.tenorite.game;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;

/**
 * @author Johan Siebens
 */
final class Occurancy {

    static <T extends Enum> Map<T, Integer> normalize(Class<T> type, Map<T, Integer> values) {
        T[] t = type.getEnumConstants();
        int[] occurancies = new int[t.length];

        for (T enumConstant : t) {
            occurancies[enumConstant.ordinal()] = values.getOrDefault(enumConstant, 0);
        }

        int sum = stream(occurancies).sum();

        if (sum != 100) {
            // equalization
            if (sum == 0) {
                int v = 100 / occurancies.length;
                for (int i = 0; i < occurancies.length; i++) {
                    occurancies[i] = v;
                }
            }
            else {
                float f = 100f / sum;
                for (int i = 0; i < occurancies.length; i++) {
                    occurancies[i] = (int) (occurancies[i] * f);
                }
            }

            // distributing points left
            sum = 0;
            for (int occurancy : occurancies) {
                sum = sum + occurancy;
            }
            int r = 100 - sum;
            int i = 0;
            while (i < occurancies.length && r > 0) {
                occurancies[i] = occurancies[i] + 1;
                r = r - 1;
                i = i + 1;
            }
        }

        return stream(t).collect(Collectors.toMap(identity(), e -> occurancies[e.ordinal()]));
    }

}
