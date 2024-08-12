/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface Mapper<T> {
    void apply(T mapping, ItemStack item, ItemMeta meta);

    default void apply(T mapping, ItemStack item, ItemMeta meta, ItemBuilder builder) {
        apply(mapping, item, meta);
    }

    @Nullable
    T convert(ItemStack item, ItemMeta meta);

    interface Complex<T> extends Mapper<T> {
        @Override
        default void apply(T mapping, ItemStack item, ItemMeta meta) {
            throw new UnsupportedOperationException();
        }

        @Override
        void apply(T mapping, ItemStack item, ItemMeta meta, ItemBuilder builder);
    }
}
