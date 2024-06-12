/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;

public class NodeCustomPersistentDataProvider implements CustomPersistentDataProvider {
    @Override
    public @NotNull PersistentDataStorage persistentData(@NotNull String table, @NotNull Key key) {
        return SynchronizedPersistentDataStorages.storageTuple(table, key)._2();
    }
}
