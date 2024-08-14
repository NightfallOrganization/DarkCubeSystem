/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.Unit;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HideAdditionalTooltipMapper implements Mapper<Unit> {
    @Override
    public void apply(Unit mapping, ItemStack item, ItemMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    }

    @Override
    public @Nullable Unit convert(ItemStack item, ItemMeta meta) {
        return meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) ? Unit.INSTANCE : null;
    }
}
