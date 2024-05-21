/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.com.google.gson.JsonElement;

/**
 * @param <T> the data type
 * @author DasBabyPixel
 */
public interface PersistentDataType<T> {

    T deserialize(JsonElement json);

    JsonElement serialize(T data);

    /**
     * @param object the object to clone
     * @return a new cloned object, or the same if immutable
     */
    T clone(T object);

}
