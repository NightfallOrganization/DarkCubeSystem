/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface CustomPersistentDataProvider {
    static CustomPersistentDataProvider dataProvider() {
        return CustomPersistentDataProviderHolder.instance();
    }

    @NotNull
    PersistentDataStorage persistentData(@NotNull String table, @NotNull Key key);
}
