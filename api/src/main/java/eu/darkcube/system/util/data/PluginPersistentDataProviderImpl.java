/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

class PluginPersistentDataProviderImpl implements PluginPersistentDataProvider {
    private static final PluginPersistentDataProvider instance = new PluginPersistentDataProviderImpl();

    private PluginPersistentDataProviderImpl() {
    }

    static PluginPersistentDataProvider instance() {
        return instance;
    }

    @Override
    public PersistentDataStorage persistentData(Key key) {
        return CustomPersistentDataProvider.dataProvider().persistentData("persistent_data", key);
    }
}
