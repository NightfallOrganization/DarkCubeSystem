/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import net.minecraft.world.item.component.CustomModelData;

public record CustomModelDataMapper() implements Mapper<Integer, CustomModelData> {
    @Override
    public CustomModelData apply(Integer mapping) {
        return new CustomModelData(mapping);
    }

    @Override
    public Integer load(CustomModelData mapping) {
        return mapping.value();
    }
}
