/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * @param <T> the data type
 * @author DasBabyPixel
 */
public interface PersistentDataType<T> {

    @NotNull
    T deserialize(@NotNull JsonElement json);

    @NotNull
    JsonElement serialize(@NotNull T data);

    /**
     * @param object the object to clone
     * @return a new cloned object, or the same if immutable
     */
    @NotNull
    T clone(@NotNull T object);

}
