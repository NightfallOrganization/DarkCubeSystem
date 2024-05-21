/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import eu.darkcube.system.provider.InternalProvider;

class PluginPersistentDataProviderHolder {
    private static final PluginPersistentDataProvider instance = InternalProvider.instance().instance(PluginPersistentDataProvider.class);

    public PluginPersistentDataProviderHolder() {
        throw new AssertionError();
    }

    static PluginPersistentDataProvider instance() {
        var instance = PluginPersistentDataProviderHolder.instance;
        if (instance == null) throw new AssertionError("PluginPersistentDataProvider not initialized");
        return instance;
    }
}
