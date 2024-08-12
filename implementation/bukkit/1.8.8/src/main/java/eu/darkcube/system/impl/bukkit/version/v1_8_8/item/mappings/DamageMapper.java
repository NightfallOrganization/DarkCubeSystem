/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class DamageMapper implements Mapper<Integer> {
    @Override
    public void apply(Integer mapping, ItemStack item, ItemMeta meta) {
        item.setDurability(mapping.shortValue());
    }

    @Override
    public Integer convert(ItemStack item, ItemMeta meta) {
        return (int) item.getDurability();
    }
}
