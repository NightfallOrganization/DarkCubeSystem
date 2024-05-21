/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version;

import eu.darkcube.system.impl.bukkit.item.BukkitEquipmentSlotProvider;
import eu.darkcube.system.impl.bukkit.item.enchant.BukkitEnchantmentProvider;
import eu.darkcube.system.impl.bukkit.item.firework.BukkitFireworkEffectProvider;
import eu.darkcube.system.impl.bukkit.item.flag.BukkitItemFlagProvider;
import eu.darkcube.system.impl.bukkit.item.material.BukkitMaterialProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.EquipmentSlotProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;
import eu.darkcube.system.version.Version;

public abstract class AbstractVersionHandler implements BukkitVersionHandler {
    public AbstractVersionHandler() {
        var version = createVersion();
        var ext = InternalProvider.instance();
        ext.register(Version.class, version);
        ext.register(ItemFlagProvider.class, new BukkitItemFlagProvider());
        ext.register(FireworkEffectProvider.class, new BukkitFireworkEffectProvider());
        ext.register(EnchantmentProvider.class, new BukkitEnchantmentProvider());
        ext.register(MaterialProvider.class, new BukkitMaterialProvider());
        ext.register(EquipmentSlotProvider.class, new BukkitEquipmentSlotProvider());
    }

    protected abstract Version createVersion();
}
