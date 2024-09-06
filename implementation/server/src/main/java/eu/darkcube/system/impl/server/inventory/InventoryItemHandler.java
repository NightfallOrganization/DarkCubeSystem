/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface InventoryItemHandler<PlatformItem, PlatformPlayer> {
    static <PlatformItem, PlatformPlayer> InventoryItemHandler<PlatformItem, PlatformPlayer> simple(@NotNull User user, @NotNull PlatformPlayer player, @NotNull TemplateInventoryImpl<PlatformItem> inventory, @NotNull InventoryTemplateImpl<PlatformPlayer> template) {
        return new SimpleItemHandler<>(user, player, inventory, template);
    }

    @NotNull
    TemplateInventoryImpl<PlatformItem> inventory();

    /**
     * Calculates all the items in the inventory.
     */
    void doOpen();

    void doClose();

    void handleClick(int slot, @NotNull PlatformItem itemStack, @NotNull ItemBuilder item);

    void updateSlots(int... slots);

    void updateSlots(int priority, int... slots);

    void startContainerTransaction(ContainerView containerView);

    void finishContainerTransaction(ContainerView containerView);

    @NotNull
    ContainerView addContainer(int priority, @NotNull Container container, @NotNull ContainerViewConfiguration configuration);

    void removeContainer(@NotNull ContainerView containerView);

    List<ContainerView> containers();

    @Nullable
    Map.Entry<ContainerView, Integer> findContainer(int slot);
}
