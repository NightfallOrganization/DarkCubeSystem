/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import net.minecraft.world.level.saveddata.maps.MapId;

public record MapIdMapper() implements DirectMapper<Integer, MapId> {
    @Override
    public MapId apply(Integer mapping) {
        return new MapId(mapping);
    }

    @Override
    public Integer load(MapId mapping) {
        return mapping.id();
    }
}
