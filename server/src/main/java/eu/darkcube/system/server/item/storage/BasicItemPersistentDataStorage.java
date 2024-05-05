/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.storage;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.LocalPersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

public class BasicItemPersistentDataStorage extends LocalPersistentDataStorage implements ItemPersistentDataStorage {
    private final ItemBuilder builder;

    public BasicItemPersistentDataStorage(ItemBuilder builder) {
        this.builder = builder;
    }

    @Override
    public <T> ItemPersistentDataStorage iset(Key key, PersistentDataType<T> type, T data) {
        set(key, type, data);
        return this;
    }

    @Override
    public <T> ItemPersistentDataStorage isetIfNotPresent(Key key, PersistentDataType<T> type, T data) {
        setIfNotPresent(key, type, data);
        return this;
    }

    @Override
    public ItemBuilder builder() {
        return builder;
    }
}
