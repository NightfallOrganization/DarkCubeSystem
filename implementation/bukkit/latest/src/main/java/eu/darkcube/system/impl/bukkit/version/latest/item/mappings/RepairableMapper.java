/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.util.ObjectSet;
import net.minecraft.world.item.enchantment.Repairable;

public record RepairableMapper() implements Mapper<ObjectSet, Repairable> {
    @Override
    public Repairable apply(ObjectSet mapping) {
        return new Repairable(MapperUtil.convertItems(mapping));
    }

    @Override
    public ObjectSet load(Repairable mapping) {
        return MapperUtil.convert(mapping.items());
    }
}
