/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8;

import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory.BukkitInventoryTemplatePlatformProvider;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory.BukkitItemTemplateItemProvider;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory.InventoryVersionProviderImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.KeyProviderImpl;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.enchant.BukkitEnchantmentProvider;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.material.BukkitMaterialProvider;
import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        install(KeyProvider.class, new KeyProviderImpl());
        install(MaterialProvider.class, new BukkitMaterialProvider());
        install(EnchantmentProvider.class, new BukkitEnchantmentProvider());
        install(ItemProvider.class, new ItemProviderImpl());
        install(InventoryVersionProvider.class, new InventoryVersionProviderImpl());
        install(DarkCubeItemTemplates.ItemProvider.class, new BukkitItemTemplateItemProvider());
        install(DarkCubeInventoryTemplates.PlatformProvider.class, new BukkitInventoryTemplatePlatformProvider());
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
