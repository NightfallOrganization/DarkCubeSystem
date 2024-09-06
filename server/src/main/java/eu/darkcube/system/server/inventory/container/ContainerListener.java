/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface ContainerListener {
    default void onItemAdded(int slot, @NotNull ItemBuilder item, int amountAdded) {
    }

    default void onItemRemoved(int slot, @NotNull ItemBuilder previousItem, int amountRemoved) {
    }

    default void onItemChanged(int slot, @NotNull ItemBuilder previousItem, @NotNull ItemBuilder newItem) {
    }
}
