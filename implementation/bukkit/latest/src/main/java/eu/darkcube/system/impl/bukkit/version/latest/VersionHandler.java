/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.impl.bukkit.version.AbstractVersionHandler;
import eu.darkcube.system.impl.bukkit.version.latest.inventory.BukkitInventoryTemplatePlatformProvider;
import eu.darkcube.system.impl.bukkit.version.latest.inventory.BukkitItemTemplateItemProvider;
import eu.darkcube.system.impl.bukkit.version.latest.inventory.InventoryVersionProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.ItemProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.KeyProviderImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierOperationProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.enchant.BukkitEnchantmentProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.flag.BukkitItemFlagProvider;
import eu.darkcube.system.impl.bukkit.version.latest.item.material.BukkitMaterialProvider;
import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperationProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        install(KeyProvider.class, new KeyProviderImpl());
        install(MaterialProvider.class, new BukkitMaterialProvider());
        install(ItemProvider.class, new ItemProviderImpl());
        install(AttributeProvider.class, new BukkitAttributeProvider());
        install(EnchantmentProvider.class, new BukkitEnchantmentProvider());
        install(ItemFlagProvider.class, new BukkitItemFlagProvider());
        install(AttributeModifierProvider.class, new BukkitAttributeModifierProvider());
        install(AttributeModifierOperationProvider.class, new BukkitAttributeModifierOperationProvider());
        install(InventoryVersionProvider.class, new InventoryVersionProviderImpl());
        install(DarkCubeItemTemplates.ItemProvider.class, new BukkitItemTemplateItemProvider());
        install(DarkCubeInventoryTemplates.PlatformProvider.class, new BukkitInventoryTemplatePlatformProvider());
    }

    @Override
    protected Version createVersion() {
        return new Version();
    }
}
