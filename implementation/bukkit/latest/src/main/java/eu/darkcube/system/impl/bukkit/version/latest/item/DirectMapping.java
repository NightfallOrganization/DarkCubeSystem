/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item;

import eu.darkcube.system.server.data.component.DataComponent;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public record DirectMapping<T, V>(DataComponent<T> component, DataComponentType<V> minecraftType, DirectMapper<T, V> mapper) {
    public <K> DirectMapping(DataComponent<K> component, DataComponentType<K> minecraftType) {
        this((DataComponent<T>) component, (DataComponentType<V>) minecraftType, (DirectMapper<T, V>) new DirectMapper<K, K>() {
            @Override
            public K apply(K mapping) {
                return mapping;
            }

            @Override
            public K load(K mapping) {
                return mapping;
            }
        });
    }

    boolean apply(ItemBuilder builder, ItemStack item) {
        if (!builder.has(component)) return false;
        var data = builder.get(component);
        var converted = mapper.apply(data);
        item.set(minecraftType, converted);
        return true;
    }

    boolean load(ItemBuilder builder, ItemStack item) {
        if (!item.has(minecraftType)) return false;
        var data = mapper.load(item.get(minecraftType));
        builder.set(component, data);
        return true;
    }
}
