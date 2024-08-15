/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.BannerPatterns;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.bukkit.craftbukkit.CraftRegistry;

public record BannerPatternsMapper() implements Mapper<BannerPatterns, BannerPatternLayers> {
    private static final Registry<BannerPattern> REGISTRY = CraftRegistry.getMinecraftRegistry(Registries.BANNER_PATTERN);

    @Override
    public BannerPatternLayers apply(BannerPatterns mapping) {
        return new BannerPatternLayers(mapping.layers().stream().map(layer -> {
            var color = MapperUtil.convert(layer.color());
            var holder = REGISTRY.getHolder(ResourceLocation.fromNamespaceAndPath(layer.pattern().namespace(), layer.pattern().value())).orElseThrow();
            return new BannerPatternLayers.Layer(holder, color);
        }).toList());
    }

    @Override
    public BannerPatterns load(BannerPatternLayers mapping) {
        return new BannerPatterns(mapping.layers().stream().map(layer -> {
            var key = Key.key(layer.pattern().unwrapKey().orElseThrow().location().toString());
            var color = MapperUtil.convert(layer.color());
            return new BannerPatterns.Layer(key, color);
        }).toList());
    }
}
