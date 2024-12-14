/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.LockCode;

public record LockMapper() implements Mapper<CompoundBinaryTag, LockCode> {
    @Override
    public LockCode apply(CompoundBinaryTag mapping) {
        var access = MinecraftServer.getServer().registryAccess();
        var nbt = MapperUtil.convert(mapping);
        return LockCode.fromTag(nbt, access);
    }

    @Override
    public CompoundBinaryTag load(LockCode mapping) {
        var nbt = new CompoundTag();
        mapping.addToTag(nbt, MinecraftServer.getServer().registryAccess());
        return MapperUtil.convert(nbt);
    }
}
