/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.LodestoneTracker;
import eu.darkcube.system.server.item.component.components.util.WorldPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record LodestoneTrackerMapper() implements Mapper<LodestoneTracker, net.minecraft.world.item.component.LodestoneTracker> {
    @Override
    public net.minecraft.world.item.component.LodestoneTracker apply(LodestoneTracker mapping) {
        return new net.minecraft.world.item.component.LodestoneTracker(Optional.ofNullable(mapping.target()).map(p -> new GlobalPos(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(p.dimension())), new BlockPos(p.x(), p.y(), p.z()))), mapping.tracked());
    }

    @Override
    public LodestoneTracker load(net.minecraft.world.item.component.LodestoneTracker mapping) {
        return new LodestoneTracker(mapping.target().map(t -> new WorldPos(t.dimension().location().toString(), t.pos().getX(), t.pos().getY(), t.pos().getZ())).orElse(null), mapping.tracked());
    }
}
