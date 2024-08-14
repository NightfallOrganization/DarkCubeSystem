/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

public record BundleContentsMapper() implements DirectMapper<List<ItemBuilder>, BundleContents> {
    @Override
    public BundleContents apply(List<ItemBuilder> mapping) {
        return new BundleContents(MapperUtil.convertItemsToMinecraft(mapping));
    }

    @Override
    public List<ItemBuilder> load(BundleContents mapping) {
        return MapperUtil.convertItemsToBuilder((List<ItemStack>) mapping.items());
    }
}
