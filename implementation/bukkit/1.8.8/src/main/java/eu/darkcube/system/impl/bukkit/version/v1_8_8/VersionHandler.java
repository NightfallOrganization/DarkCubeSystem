/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8;

import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory.InventoryVersionProviderImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.KeyProviderImpl;
import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.ItemProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        InternalProvider.instance().register(ItemProvider.class, new ItemProviderImpl());
        InternalProvider.instance().register(KeyProvider.class, new KeyProviderImpl());
        InternalProvider.instance().register(InventoryVersionProvider.class, new InventoryVersionProviderImpl());
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
