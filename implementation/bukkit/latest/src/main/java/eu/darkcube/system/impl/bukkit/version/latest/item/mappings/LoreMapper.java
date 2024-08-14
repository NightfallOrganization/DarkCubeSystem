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
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import net.minecraft.world.item.component.ItemLore;

public record LoreMapper() implements DirectMapper<List<Component>, ItemLore> {
    @Override
    public ItemLore apply(List<Component> mapping) {
        return new ItemLore(mapping.stream().map(MapperUtil::convert).toList());
    }

    @Override
    public List<Component> load(ItemLore mapping) {
        return mapping.lines().stream().map(MapperUtil::convert).toList();
    }
}
