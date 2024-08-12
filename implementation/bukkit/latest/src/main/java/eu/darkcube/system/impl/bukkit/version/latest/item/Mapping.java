/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item;

import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record Mapping<T>(DataComponent<T> component, Mapper<T> mapper) {
    boolean apply(ItemBuilder builder, ItemStack item, ItemMeta meta) {
        if (!builder.has(component)) return false;
        var data = builder.get(component);
        mapper.apply(data, item, meta, builder);
        return true;
    }

    boolean load(ItemBuilder builder, ItemStack item, ItemMeta meta) {
        var data = mapper.convert(item, meta);
        if (data == null) return false;
        builder.set(component, data);
        return true;
    }
}
