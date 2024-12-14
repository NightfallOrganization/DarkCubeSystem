/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.ArmorTrim;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public record TrimMapper() implements Mapper<ArmorTrim, net.minecraft.world.item.equipment.trim.ArmorTrim> {
    private static final RegistryOps<Tag> OPS = RegistryOps.create(NbtOps.INSTANCE, MinecraftServer.getServer().registryAccess());

    @Override
    public net.minecraft.world.item.equipment.trim.ArmorTrim apply(ArmorTrim mapping) {
        var material = OPS.getter(Registries.TRIM_MATERIAL).orElseThrow().get(ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.parse(mapping.material().asString()))).orElseThrow();
        var pattern = OPS.getter(Registries.TRIM_PATTERN).orElseThrow().get(ResourceKey.create(Registries.TRIM_PATTERN, ResourceLocation.parse(mapping.pattern().asString()))).orElseThrow();
        return new net.minecraft.world.item.equipment.trim.ArmorTrim(material, pattern, mapping.showInTooltip());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public ArmorTrim load(net.minecraft.world.item.equipment.trim.ArmorTrim mapping) {
        return new ArmorTrim(Key.key(mapping.material().unwrapKey().orElseThrow().location().toString()), Key.key(mapping.pattern().unwrapKey().orElseThrow().location().toString()), mapping.showInTooltip());
    }
}
