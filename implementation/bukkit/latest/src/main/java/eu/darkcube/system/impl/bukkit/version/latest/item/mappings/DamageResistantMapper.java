/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.DamageResistant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record DamageResistantMapper() implements Mapper<DamageResistant, net.minecraft.world.item.component.DamageResistant> {
    @Override
    public net.minecraft.world.item.component.DamageResistant apply(DamageResistant mapping) {
        return new net.minecraft.world.item.component.DamageResistant(TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(mapping.tag())));
    }

    @Override
    public DamageResistant load(net.minecraft.world.item.component.DamageResistant mapping) {
        return new DamageResistant(mapping.types().location().toString());
    }
}
