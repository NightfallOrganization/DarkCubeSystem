/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.storage;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

public interface ItemPersistentDataStorage extends PersistentDataStorage {

    @NotNull
    default <T> ItemPersistentDataStorage iset(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        set(key, type, data);
        return this;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    default <T> ItemPersistentDataStorage isetIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        return isetIfAbsent(key, type, data);
    }

    @NotNull
    default <T> ItemPersistentDataStorage isetIfAbsent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        setIfAbsent(key, type, data);
        return this;
    }

    @NotNull
    default <T> ItemPersistentDataStorage iset(@NotNull DataKey<T> key, @NotNull T data) {
        set(key, data);
        return this;
    }

    @NotNull
    default <T> ItemPersistentDataStorage isetIfAbsent(@NotNull DataKey<T> key, @NotNull T data) {
        setIfAbsent(key, data);
        return this;
    }

    @NotNull
    ItemBuilder builder();

}
