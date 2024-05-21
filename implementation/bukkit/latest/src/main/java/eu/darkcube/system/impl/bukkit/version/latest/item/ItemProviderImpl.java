/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item;

import eu.darkcube.system.impl.bukkit.item.BukkitItemProvider;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;
import org.bukkit.inventory.ItemStack;

public class ItemProviderImpl implements BukkitItemProvider {
    @Override public @NotNull ItemBuilder item(@NotNull ItemStack item) {
        return new ItemBuilderImpl(item);
    }

    @Override public @NotNull ItemBuilder item(@Nullable Material material) {
        return new ItemBuilderImpl().material(Material.ofNullable(material));
    }

    @Override public @NotNull ItemBuilder item(@NotNull JsonElement json) {
        return ItemBuilderImpl.deserialize(json);
    }
}
