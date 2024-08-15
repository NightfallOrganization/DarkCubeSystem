/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomNameMapper implements Mapper<Component> {
    @Override
    public void apply(Component mapping, ItemStack item, ItemMeta meta) {
        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(mapping));
    }

    @Override
    public @Nullable Component convert(ItemStack item, ItemMeta meta) {
        if (!meta.hasDisplayName()) return null;
        return LegacyComponentSerializer.legacySection().deserialize(meta.getDisplayName());
    }
}
