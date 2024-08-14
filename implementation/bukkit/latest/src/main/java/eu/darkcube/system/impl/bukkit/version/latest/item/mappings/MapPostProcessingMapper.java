/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.server.item.component.components.MapPostProcessing;

public record MapPostProcessingMapper() implements DirectMapper<MapPostProcessing, net.minecraft.world.item.component.MapPostProcessing> {
    @Override
    public net.minecraft.world.item.component.MapPostProcessing apply(MapPostProcessing mapping) {
        return switch (mapping) {
            case LOCK -> net.minecraft.world.item.component.MapPostProcessing.LOCK;
            case SCALE -> net.minecraft.world.item.component.MapPostProcessing.SCALE;
        };
    }

    @Override
    public MapPostProcessing load(net.minecraft.world.item.component.MapPostProcessing mapping) {
        return switch (mapping) {
            case LOCK -> MapPostProcessing.LOCK;
            case SCALE -> MapPostProcessing.SCALE;
        };
    }
}
