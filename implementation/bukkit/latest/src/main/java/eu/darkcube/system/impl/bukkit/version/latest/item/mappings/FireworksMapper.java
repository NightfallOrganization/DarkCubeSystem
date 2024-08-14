/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.FireworkList;
import net.minecraft.world.item.component.Fireworks;

public record FireworksMapper() implements Mapper<FireworkList, Fireworks> {
    private static final FireworkExplosionMapper m = new FireworkExplosionMapper();

    @Override
    public Fireworks apply(FireworkList mapping) {
        return new Fireworks(mapping.flightDuration(), mapping.explosions().stream().map(m::apply).toList());
    }

    @Override
    public FireworkList load(Fireworks mapping) {
        return new FireworkList(mapping.flightDuration(), mapping.explosions().stream().map(m::load).toList());
    }
}
