/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerView;
import eu.darkcube.system.server.inventory.container.ContainerViewConfiguration;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;

/**
 * Represents an inventory created from an {@link InventoryTemplate}
 */
public interface TemplateInventory extends Inventory {
    @Api
    @NotNull
    PagedInventoryController pagedController();

    @Api
    void updateSlotsAtPriority(int priority, int @NotNull ... slots);

    @Api
    void updateSlots(int @NotNull ... slots);

    @Api
    @NotNull
    ContainerView addContainer(int priority, @NotNull Container container);

    @Api
    @NotNull
    ContainerView addContainer(int priority, @NotNull Container container, @NotNull ContainerViewConfiguration configuration);

    void addListener(@NotNull TemplateInventoryListener listener);

    void removeListener(@NotNull TemplateInventoryListener listener);

    @Api
    void removeContainer(@NotNull ContainerView view);
}
