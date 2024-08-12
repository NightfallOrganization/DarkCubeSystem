/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.lang.reflect.Field;
import java.util.Map;

import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SpawnerEntityDataMapper implements Mapper<String> {
    private static final Field CraftMetaItem$unhandledTags = ReflectionUtils.getField(ReflectionUtils.getClass("CraftMetaItem", ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), true, "unhandledTags");

    @Override
    public void apply(String mapping, ItemStack item, ItemMeta meta) {
        var tags = (Map<String, NBTBase>) ReflectionUtils.getValue(meta, CraftMetaItem$unhandledTags);
        tags.put("EntityTag", new NBTTagString(mapping));
    }

    @Override
    public @Nullable String convert(ItemStack item, ItemMeta meta) {
        var tags = (Map<String, NBTBase>) ReflectionUtils.getValue(meta, CraftMetaItem$unhandledTags);
        if (!tags.containsKey("EntityTag")) return null;
        return ((NBTTagString) tags.get("EntityTag")).a_();
    }
}
