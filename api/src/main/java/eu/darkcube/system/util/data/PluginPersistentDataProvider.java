/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public interface PluginPersistentDataProvider {
    static PluginPersistentDataProvider pluginPersistentDataProvider() {
        return PluginPersistentDataProviderHolder.instance();
    }

    PersistentDataStorage persistentData(Key key);
}
