/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minecraft.world.item.component.ChargedProjectiles;

public record ChargedProjectilesMapper() implements Mapper<List<ItemBuilder>, ChargedProjectiles> {
    @Override
    public ChargedProjectiles apply(List<ItemBuilder> mapping) {
        return ChargedProjectiles.of(MapperUtil.convertItemsToMinecraft(mapping));
    }

    @Override
    public List<ItemBuilder> load(ChargedProjectiles mapping) {
        return MapperUtil.convertItemsToBuilder(mapping.getItems());
    }
}
