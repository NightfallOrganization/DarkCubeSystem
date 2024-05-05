/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import java.util.Collection;
import java.util.function.Supplier;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

/**
 * All inventories extend audience to forward messages and sounds to all the viewers.
 */
public interface Inventory extends Audience {
    static @NotNull InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType) {
        return InventoryProviderImpl.inventoryProvider().createTemplate(key, inventoryType);
    }

    static @NotNull InventoryTemplate createChestTemplate(@NotNull Key key, int size) {
        return InventoryProviderImpl.inventoryProvider().createChestTemplate(key, size);
    }

    static @NotNull PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title) {
        return InventoryProviderImpl.inventoryProvider().prepare(inventoryType, title);
    }

    static @NotNull PreparedInventory prepareChest(int size, @NotNull Component title) {
        return InventoryProviderImpl.inventoryProvider().prepareChest(size, title);
    }

    @Nullable
    Component title();

    /**
     * Set the item at a slot.
     * <p>
     * Accepted Types:
     * <ul>
     *     <li>{@link ItemBuilder}</li>
     *     <li>Platform-Specific ItemStack</li>
     *     <li>{@link Supplier} of any of the accepted types</li>
     * </ul>
     *
     * @param slot the slot to set the item in
     * @param item the item to set
     */
    void setItem(int slot, @NotNull Object item);

    /**
     * Get the item at a slot.
     *
     * @param slot the slot
     * @return the calculated item
     */
    @NotNull
    ItemBuilder getItem(int slot);

    int size();

    @Unmodifiable
    @NotNull
    Collection<@NotNull InventoryListener> listeners();

    @NotNull
    InventoryType type();

    void addListener(@NotNull InventoryListener listener);

    void removeListener(@NotNull InventoryListener listener);

    /**
     * Opens the inventory for a player.
     * <p>
     * Allowed types are:
     * <ul>
     * <li>{@link User} (will only work if online)</li>
     * <li>Platform-Specific Types</li>
     * </ul>
     *
     * @param player the player
     */
    void open(@NotNull Object player);

    /**
     * Checks if a player has this inventory open.
     * <p>
     * Allowed types are same as {@link #open(Object)}
     *
     * @param player the player
     * @return whether player has the inventory opened
     */
    boolean opened(@NotNull Object player);

    /**
     * @return the users that have this inventory open
     */
    @NotNull
    @Unmodifiable
    Collection<User> opened();
}
