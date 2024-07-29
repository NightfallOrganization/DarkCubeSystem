/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface InventoryItemHandler<PlatformItem, PlatformPlayer> {
    static <PlatformItem, PlatformPlayer> InventoryItemHandler<PlatformItem, PlatformPlayer> simple(TemplateInventoryImpl<PlatformItem> inventory, InventoryTemplateImpl<PlatformPlayer> template) {
        return new SimpleItemHandler<>(inventory, template);
    }

    @NotNull
    TemplateInventoryImpl<PlatformItem> inventory();

    /**
     * Calculates all the items in the inventory.
     */
    void doOpen(@NotNull PlatformPlayer player, @NotNull User user);

    void doClose(@NotNull PlatformPlayer player, @NotNull User user);

    void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item);

    void updateSlots(int... slots);

    void updateSlots(int priority, int... slots);
}
