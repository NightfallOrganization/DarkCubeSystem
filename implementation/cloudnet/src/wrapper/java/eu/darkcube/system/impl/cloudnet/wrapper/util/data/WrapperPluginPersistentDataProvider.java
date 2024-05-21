/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.util.data;

import eu.darkcube.system.cloudnet.util.data.SynchronizedPersistentDataStorage;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PluginPersistentDataProvider;

public class WrapperPluginPersistentDataProvider implements PluginPersistentDataProvider {
    @Override
    public PersistentDataStorage persistentData(Key key) {
        return new SynchronizedPersistentDataStorage(key);
    }
}
