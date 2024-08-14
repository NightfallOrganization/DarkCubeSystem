/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.material.BukkitMaterialImpl;
import eu.darkcube.system.server.item.component.components.PotDecorations;
import eu.darkcube.system.server.item.material.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record PotDecorationsMapper() implements DirectMapper<PotDecorations, net.minecraft.world.level.block.entity.PotDecorations> {
    @Override
    public net.minecraft.world.level.block.entity.PotDecorations apply(PotDecorations mapping) {

        return new net.minecraft.world.level.block.entity.PotDecorations(get(mapping.back()), get(mapping.left()), get(mapping.right()), get(mapping.front()));
    }

    private Item get(Material material) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(((BukkitMaterialImpl) material).key().asString()));
    }

    private Material get(Optional<Item> item) {
        return item.map(i -> Material.of(BuiltInRegistries.ITEM.getKey(i).toString())).orElse(Material.air());
    }

    @Override
    public PotDecorations load(net.minecraft.world.level.block.entity.PotDecorations mapping) {
        return new PotDecorations(get(mapping.back()), get(mapping.left()), get(mapping.right()), get(mapping.front()));
    }
}
