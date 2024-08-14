/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.server.item.component.components.ArmorTrim;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;

public record TrimMapper() implements DirectMapper<ArmorTrim, net.minecraft.world.item.armortrim.ArmorTrim> {
    private static final RegistryOps<Tag> OPS = RegistryOps.create(NbtOps.INSTANCE, MinecraftServer.getServer().registryAccess());

    @Override
    public net.minecraft.world.item.armortrim.ArmorTrim apply(ArmorTrim mapping) {
        var material = mapping.material();
        var pattern = mapping.pattern();
        return new net.minecraft.world.item.armortrim.ArmorTrim(material, pattern, mapping.showInTooltip());
    }

    @Override
    public ArmorTrim load(net.minecraft.world.item.armortrim.ArmorTrim mapping) {
        return null;
    }
}
