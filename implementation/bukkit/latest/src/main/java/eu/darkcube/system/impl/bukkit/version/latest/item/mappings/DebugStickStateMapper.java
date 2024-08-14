/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.HashMap;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.DebugStickState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record DebugStickStateMapper() implements Mapper<DebugStickState, net.minecraft.world.item.component.DebugStickState> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugStickStateMapper.class);

    @Override
    public net.minecraft.world.item.component.DebugStickState apply(DebugStickState mapping) {
        var map = new HashMap<Holder<Block>, Property<?>>();
        for (var entry : mapping.state().entrySet()) {
            var id = ResourceLocation.tryParse(entry.getKey());
            if (id == null) {
                LOGGER.error("Not a valid ResourceLocation: {}", entry.getKey());
                continue;
            }
            var block = BuiltInRegistries.BLOCK.getHolder(id).orElse(null);
            if (block == null) {
                LOGGER.error("Unknown block: {}", id);
                continue;
            }
            var property = block.value().getStateDefinition().getProperty(entry.getValue());
            if (property == null) {
                LOGGER.error("Unknown property {} on block {}", entry.getValue(), id);
                continue;
            }
            map.put(block, property);
        }
        return new net.minecraft.world.item.component.DebugStickState(map);
    }

    @Override
    public DebugStickState load(net.minecraft.world.item.component.DebugStickState mapping) {
        var map = new HashMap<String, String>();
        for (var entry : mapping.properties().entrySet()) {
            map.put(entry.getKey().unwrapKey().orElseThrow().location().toString(), entry.getValue().getName());
        }
        return new DebugStickState(map);
    }
}
