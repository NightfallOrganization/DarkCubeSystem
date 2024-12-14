/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import net.minecraft.world.item.component.OminousBottleAmplifier;

public record OminousBottleAmplifierMapper() implements Mapper<Integer, OminousBottleAmplifier> {
    @Override
    public OminousBottleAmplifier apply(Integer mapping) {
        return new OminousBottleAmplifier(mapping);
    }

    @Override
    public Integer load(OminousBottleAmplifier mapping) {
        return mapping.value();
    }
}
