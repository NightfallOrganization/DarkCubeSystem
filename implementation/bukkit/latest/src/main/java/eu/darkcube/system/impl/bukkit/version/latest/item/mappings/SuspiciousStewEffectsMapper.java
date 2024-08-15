/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.SuspiciousStewEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public record SuspiciousStewEffectsMapper() implements Mapper<SuspiciousStewEffects, net.minecraft.world.item.component.SuspiciousStewEffects> {
    @Override
    public net.minecraft.world.item.component.SuspiciousStewEffects apply(SuspiciousStewEffects mapping) {
        return new net.minecraft.world.item.component.SuspiciousStewEffects(mapping.effects().stream().map(e -> new net.minecraft.world.item.component.SuspiciousStewEffects.Entry(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(e.id().asString())).orElseThrow(), e.durationTicks())).toList());
    }

    @Override
    public SuspiciousStewEffects load(net.minecraft.world.item.component.SuspiciousStewEffects mapping) {
        return new SuspiciousStewEffects(mapping.effects().stream().map(e -> new SuspiciousStewEffects.Effect(Key.key(e.effect().unwrapKey().orElseThrow().location().toString()), e.duration())).toList());
    }
}
