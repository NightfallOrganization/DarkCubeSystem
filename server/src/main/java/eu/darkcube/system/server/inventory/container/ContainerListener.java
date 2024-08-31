package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface ContainerListener {
    default void onItemAdded(int slot, @NotNull ItemBuilder item) {
    }

    default void onItemRemoved(int slot, @NotNull ItemBuilder previousItem) {
    }

    default void onItemChanged(int slot, @NotNull ItemBuilder previousItem, @NotNull ItemBuilder newItem) {
    }
}
