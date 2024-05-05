/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.storage;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

public interface ItemPersistentDataStorage extends PersistentDataStorage {

    <T> ItemPersistentDataStorage iset(Key key, PersistentDataType<T> type, T data);

    <T> ItemPersistentDataStorage isetIfNotPresent(Key key, PersistentDataType<T> type, T data);

    ItemBuilder builder();

}
