/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.latest.inventory.InventoryVersionProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.KeyProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierOperationProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeProvider;
import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperationProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        InternalProvider.instance().register(KeyProvider.class, new KeyProviderImpl());
        InternalProvider.instance().register(ItemProvider.class, new ItemProviderImpl());
        InternalProvider.instance().register(AttributeProvider.class, new BukkitAttributeProvider());
        InternalProvider.instance().register(AttributeModifierProvider.class, new BukkitAttributeModifierProvider());
        InternalProvider.instance().register(AttributeModifierOperationProvider.class, new BukkitAttributeModifierOperationProvider());
        InternalProvider.instance().register(InventoryVersionProvider.class, new InventoryVersionProviderImpl());
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
