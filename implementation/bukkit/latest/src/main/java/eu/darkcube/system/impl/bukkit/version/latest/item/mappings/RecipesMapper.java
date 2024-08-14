/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import net.minecraft.resources.ResourceLocation;

public record RecipesMapper() implements DirectMapper<List<String>, List<ResourceLocation>> {
    @Override
    public List<ResourceLocation> apply(List<String> mapping) {
        return mapping.stream().map(ResourceLocation::parse).toList();
    }

    @Override
    public List<String> load(List<ResourceLocation> mapping) {
        return mapping.stream().map(ResourceLocation::toString).toList();
    }
}
