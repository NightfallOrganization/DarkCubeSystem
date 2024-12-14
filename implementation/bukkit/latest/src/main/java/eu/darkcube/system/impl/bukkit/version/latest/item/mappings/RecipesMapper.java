/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public record RecipesMapper() implements Mapper<List<String>, List<ResourceKey<Recipe<?>>>> {
    @Override
    public List<ResourceKey<Recipe<?>>> apply(List<String> mapping) {
        return mapping.stream().map(ResourceLocation::parse).map(l -> ResourceKey.create(Registries.RECIPE, l)).toList();
    }

    @Override
    public List<String> load(List<ResourceKey<Recipe<?>>> mapping) {
        return mapping.stream().map(k -> k.location().toString()).toList();
    }
}
