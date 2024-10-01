/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.item.storage;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.storage.ItemPersistentDataStorage;
import eu.darkcube.system.util.data.LocalPersistentDataStorage;

public class BasicItemPersistentDataStorage extends LocalPersistentDataStorage implements ItemPersistentDataStorage {
    private final ItemBuilder builder;

    public BasicItemPersistentDataStorage(ItemBuilder builder) {
        this.builder = builder;
    }

    @Override
    public @NotNull ItemBuilder builder() {
        return builder;
    }
}
