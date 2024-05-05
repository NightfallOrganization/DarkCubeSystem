/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Arrays {
    @SafeVarargs
    public static <T> T[] insert(T[] array, T[] instance, T... insertion) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        Collections.addAll(list, insertion);
        return list.toArray(instance);
    }
}
