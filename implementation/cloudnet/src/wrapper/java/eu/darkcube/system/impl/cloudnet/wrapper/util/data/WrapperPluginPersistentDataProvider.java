/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.util.data;

import eu.darkcube.system.cloudnet.util.data.SynchronizedPersistentDataStorage;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;

public class WrapperPluginPersistentDataProvider implements CustomPersistentDataProvider {
    @Override
    public @NotNull PersistentDataStorage persistentData(@NotNull String table, @NotNull Key key) {
        return new SynchronizedPersistentDataStorage(table, key);
    }
}
