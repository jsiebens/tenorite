package net.tenorite.util;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;

public final class Occurancy {

    public static <T extends Enum> Map<T, Integer> normalize(Class<T> type, Map<T, Integer> values) {
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
