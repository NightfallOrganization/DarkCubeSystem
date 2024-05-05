/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

public interface MetaDataStorage {

    /**
     * Stores some data
     *
     * @param key   the key
     * @param value the value
     */
    void set(Key key, Object value);

    /**
     * @param <T> the type
     * @param key the key
     * @return stored data, null if not present
     */
    <T> T get(Key key);

    /**
     * @param <T>    the type
     * @param key    the key
     * @param orElse the default value
     * @return stored data, the given fallback data if not present
     */
    <T> T getOr(Key key, T orElse);

    /**
     * @param key the key
     * @return whether this storage has data under the given key
     */
    boolean has(Key key);

    /**
     * Removes some data
     *
     * @param <T> the type
     * @param key the key
     * @return the removed data
     */
    <T> T remove(Key key);

    void clear();

}
