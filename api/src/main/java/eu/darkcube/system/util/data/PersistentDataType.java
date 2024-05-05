/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.cloudnetservice.driver.document.Document;

/**
 * @param <T> the data type
 * @author DasBabyPixel
 */
public interface PersistentDataType<T> {

    T deserialize(Document doc, String key);

    void serialize(Document.Mutable doc, String key, T data);

    /**
     * @param object the object to clone
     * @return a new cloned object, or the same if immutable
     */
    T clone(T object);

}
