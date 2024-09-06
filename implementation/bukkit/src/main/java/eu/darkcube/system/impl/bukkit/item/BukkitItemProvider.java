/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.material.Material;
import org.bukkit.inventory.ItemStack;

public interface BukkitItemProvider extends ItemProvider {
    @Override
    default @NotNull ItemBuilder item(@NotNull Object object) {
        return switch (object) {
            case Material material -> item(material);
            case org.bukkit.Material material -> item(Material.of(material));
            case ItemStack item -> item(item);
            case ItemBuilder builder -> builder.clone();
            default -> throw new IllegalArgumentException("Invalid item input: " + object);
        };
    }

    @NotNull
    ItemBuilder item(@NotNull ItemStack item);
}
