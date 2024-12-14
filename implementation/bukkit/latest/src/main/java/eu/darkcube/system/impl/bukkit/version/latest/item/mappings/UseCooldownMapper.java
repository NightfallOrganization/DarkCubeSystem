/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.UseCooldown;
import net.minecraft.resources.ResourceLocation;

public record UseCooldownMapper() implements Mapper<UseCooldown, net.minecraft.world.item.component.UseCooldown> {
    @Override
    public net.minecraft.world.item.component.UseCooldown apply(UseCooldown mapping) {
        return new net.minecraft.world.item.component.UseCooldown(mapping.seconds(), mapping.cooldownGroup() == null ? Optional.empty() : Optional.of(ResourceLocation.parse(mapping.cooldownGroup().asString())));
    }

    @Override
    public UseCooldown load(net.minecraft.world.item.component.UseCooldown mapping) {
        return new UseCooldown(mapping.seconds(), mapping.cooldownGroup().map(ResourceLocation::toString).map(Key::key).orElse(null));
    }
}
