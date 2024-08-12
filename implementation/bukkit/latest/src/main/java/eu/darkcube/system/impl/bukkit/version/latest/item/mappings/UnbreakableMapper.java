/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.Unbreakable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record UnbreakableMapper() implements Mapper<Unbreakable> {
    @Override
    public void apply(Unbreakable mapping, ItemStack item, ItemMeta meta) {
        meta.setUnbreakable(true);
        if (!mapping.showInTooltip()) {
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
    }

    @Override
    public @Nullable Unbreakable convert(ItemStack item, ItemMeta meta) {
        if (!meta.isUnbreakable()) return null;
        return new Unbreakable(!meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
    }
}
