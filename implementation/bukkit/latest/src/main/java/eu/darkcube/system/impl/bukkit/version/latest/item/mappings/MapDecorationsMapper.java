/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Map;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.server.item.component.components.MapDecorations;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorationsMapper() implements DirectMapper<MapDecorations, net.minecraft.world.item.component.MapDecorations> {
    @Override
    public net.minecraft.world.item.component.MapDecorations apply(MapDecorations mapping) {
        return new net.minecraft.world.item.component.MapDecorations(Map.ofEntries(mapping.decorations().entrySet().stream().map(e -> Map.entry(e.getKey(), new net.minecraft.world.item.component.MapDecorations.Entry(get(e.getValue().type()), e.getValue().x(), e.getValue().z(), e.getValue().rotation()))).toArray(Map.Entry[]::new)));
    }

    private Holder<MapDecorationType> get(String type) {
        return BuiltInRegistries.MAP_DECORATION_TYPE.getHolder(ResourceLocation.parse(type)).orElseThrow();
    }

    @Override
    public MapDecorations load(net.minecraft.world.item.component.MapDecorations mapping) {
        return new MapDecorations(Map.ofEntries(mapping.decorations().entrySet().stream().map(e -> Map.entry(e.getKey(), new MapDecorations.Entry(e.getValue().type().unwrapKey().orElseThrow().location().toString(), e.getValue().x(), e.getValue().z(), e.getValue().rotation()))).toArray(Map.Entry[]::new)));
    }
}
