/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.Bee;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

public record BeesMapper() implements DirectMapper<List<Bee>, List<BeehiveBlockEntity.Occupant>> {
    @Override
    public List<BeehiveBlockEntity.Occupant> apply(List<Bee> mapping) {
        return mapping.stream().map(bee -> new BeehiveBlockEntity.Occupant(MapperUtil.convertData(bee.entityData()), bee.ticksInHive(), bee.minTicksInHive())).toList();
    }

    @Override
    public List<Bee> load(List<BeehiveBlockEntity.Occupant> mapping) {
        return mapping.stream().map(bee -> new Bee(MapperUtil.convertData(bee.entityData()), bee.ticksInHive(), bee.minTicksInHive())).toList();
    }
}
