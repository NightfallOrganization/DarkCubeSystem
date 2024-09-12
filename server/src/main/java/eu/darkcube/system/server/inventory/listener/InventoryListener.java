/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.listener;

import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface InventoryListener {
    static @NotNull InventoryListener ofStateful(@NotNull Supplier<@NotNull InventoryListener> listener) {
        return InventoryListenerProviderImpl.listenerProvider().ofStateful(listener);
    }

    /**
     * Called before a player has opened an inventory
     *
     * @param inventory the inventory
     * @param user      the player
     */
    default void onPreOpen(@NotNull Inventory inventory, @NotNull User user) {
    }

    /**
     * Called after a player has opened an inventory
     *
     * @param inventory the inventory
     * @param user      the player
     */
    default void onOpen(@NotNull Inventory inventory, @NotNull User user) {
    }

    /**
     * Called everytime the inventory is updated with new content
     * (max once per tick)
     *
     * @param inventory the inventory
     */
    default void onUpdate(@NotNull Inventory inventory) {
    }

    /**
     * Called every time a slot is updated with a new item.
     * To get the item, use {@link Inventory#getItem(int)}.
     *
     * @param inventory the inventory
     * @param slot      the slot
     */
    default void onSlotUpdate(@NotNull Inventory inventory, int slot) {
    }

    /**
     * Called after a player has closed an inventory
     *
     * @param inventory the inventory that was closed
     * @param user      the user that closed the inventory
     */
    default void onClose(@NotNull Inventory inventory, @NotNull User user) {
    }

    /**
     * Called when an item in an inventory is clicked.
     * Inventories are unmodifiable, so click events are cancelled.
     *
     * @param inventory the affected inventory
     * @param user      the user that clicked
     * @param slot      the slot the item is in
     * @param item      the item
     * @deprecated use {@link #onClick(Inventory, User, int, ItemBuilder, ClickData)}
     */
    @Deprecated
    default void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
    }

    /**
     * Called when an item in an inventory is clicked.
     * Inventories are unmodifiable, so click events are cancelled.
     *
     * @param inventory the affected inventory
     * @param user      the user that clicked
     * @param slot      the slot the item is in
     * @param item      the item
     * @param clickData data about the click
     */
    default void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
        onClick(inventory, user, slot, item);
    }
}
