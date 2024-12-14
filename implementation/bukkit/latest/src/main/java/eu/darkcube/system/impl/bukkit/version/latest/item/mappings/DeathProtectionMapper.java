/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.DeathProtection;

public record DeathProtectionMapper() implements Mapper<DeathProtection, net.minecraft.world.item.component.DeathProtection> {
    @Override
    public net.minecraft.world.item.component.DeathProtection apply(DeathProtection mapping) {
        return new net.minecraft.world.item.component.DeathProtection(mapping.deathEffects().stream().map(MapperUtil::convert).toList());
    }

    @Override
    public DeathProtection load(net.minecraft.world.item.component.DeathProtection mapping) {
        return new DeathProtection(mapping.deathEffects().stream().map(MapperUtil::convert).toList());
    }
}
