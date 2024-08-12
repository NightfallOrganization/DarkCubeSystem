/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class LoreMapper implements Mapper<List<Component>> {
    @Override
    public void apply(List<Component> mapping, ItemStack item, ItemMeta meta) {
        var lore = new ArrayList<String>();
        for (var component : mapping) {
            var l = LegacyComponentSerializer.legacySection().serialize(component);
            String last = null;
            for (var line : l.split("\\R")) {
                if (last != null) line = last + line;
                last = ChatColor.getLastColors(line);
                lore.add(line);
            }
        }
        meta.setLore(lore);
    }

    @Override
    public @Nullable List<Component> convert(ItemStack item, ItemMeta meta) {
        if (!meta.hasLore()) return null;
        return meta.getLore().stream().<Component>map(LegacyComponentSerializer.legacySection()::deserialize).toList();
    }
}
