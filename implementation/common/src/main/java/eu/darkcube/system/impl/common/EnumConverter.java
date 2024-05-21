/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common;

import java.lang.reflect.Array;
import java.util.function.Function;

public interface EnumConverter {
    static <T, V extends Enum<V>> T[] convert(Class<V> type, Class<T> resultType, Function<V, T> converter) {
        var values = type.getEnumConstants();
        var result = (T[]) Array.newInstance(resultType, values.length);
        for (var i = 0; i < values.length; i++) {
            result[i] = converter.apply(values[i]);
        }
        return result;
    }
}
