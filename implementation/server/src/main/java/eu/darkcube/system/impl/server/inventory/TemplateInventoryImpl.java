/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface TemplateInventoryImpl<PlatformItem> extends TemplateInventory {
    static @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] deepCopy(@Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] maps) {
        var result = new SortedMap[maps.length];
        for (var i = 0; i < result.length; i++) {
            var data = maps[i];
            if (data == null) continue;
            var map = new TreeMap<Integer, ItemReferenceImpl>(Comparator.reverseOrder());
            result[i] = map;
            for (var entry : data.entrySet()) {
                map.put(entry.getKey(), entry.getValue().clone());
            }
        }
        return result;
    }

    @NotNull
    Instant openInstant();

    void scheduleSetItem(int slot, @NotNull Duration duration, @NotNull PlatformItem item);

    @Nullable
    PlatformItem computeItem(@Nullable User user, @Nullable Object item);

    void onMainThread(@NotNull Runnable runnable);

    @NotNull
    List<TemplateInventoryListener> templateListeners();

    void setAir(int slot);

    @Override
    default @NotNull ContainerView addContainer(int priority, @NotNull Container container) {
        return addContainer(priority, container, ContainerViewConfiguration.EMPTY);
    }

    void returnItemToUser(ItemBuilder item);
}
