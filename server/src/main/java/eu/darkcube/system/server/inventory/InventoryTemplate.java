/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

/**
 * Represents an inventory with unknown implementation.
 * <p>
 * Can be used for...
 * <ul>
 * <li>normal inventories without additional features.</li>
 * <li>animated inventories</li>
 * <li>paged inventories</li>
 * <li>animated paged inventories</li>
 * <li>async implementations of the above</li>
 * </ul>
 * <p>
 * Something to note is that the underlying inventory is the same for every user.
 */
public interface InventoryTemplate extends Keyed {
    /**
     * Gets the {@link InventoryType} of this inventory.
     * Inventories can have different layouts, like Anvils, Chests, Furnaces, ...
     * These layouts are called {@link InventoryType InventoryTypes}
     *
     * @return this inventory's type
     */
    @NotNull
    InventoryType type();

    int size();

    /*
    Normal
    Animated
    Paged
    Async
    Sounds

    Animations:
    Duration to wait per slot (Duration[size])


    setItem(slot, ItemStack);
    setItem(slot, translatingItem);
    setItem(slot, userToItemFunction);

    Function<User, ItemStack>

     */

    /**
     * Gets the {@link AnimatedTemplateSettings} for this inventory.
     * This allows configuration for animations.
     *
     * @return the animation settings
     */
    @NotNull
    AnimatedTemplateSettings animation();

    /**
     * Gets the {@link PagedTemplateSettings} for this inventory.
     * This allows configuration of an inventory with multiple pages if the items may
     * not fit into a single inventory.
     *
     * @return the pagination settings
     */
    @NotNull
    PagedTemplateSettings pagination();

    /**
     * Sets the title.
     * <p>
     * Accepted types:
     * <ul>
     *  <li>{@link BaseMessage}</li>
     *  <li>{@link Component}</li>
     *  <li>String</li>
     *  <li>Platform-Specific Messages</li>
     * </ul>
     *
     * @param title the title
     */
    void title(@Nullable Object title);

    /**
     * Gets a copy of all the listener
     *
     * @return Collection containing the listeners
     */
    @Unmodifiable
    @NotNull
    Collection<InventoryListener> listeners();

    /**
     * Adds an {@link InventoryListener} to this template
     *
     * @param listener the listener to add
     */
    void addListener(@NotNull InventoryListener listener);

    /**
     * Removes an {@link InventoryListener} from this template
     *
     * @param listener the listener to remove
     */
    void removeListener(@NotNull InventoryListener listener);

    /**
     * Sets an item.
     * Allowed types for {@code item} are
     * <ul>
     *     <li>{@link ItemBuilder}</li>
     *     <li>Platform-Specific ItemStack</li>
     *     <li>{@link ItemFactory}</li>
     *     <li>{@link Supplier} of any of the accepted types</li>
     *     <li>{@link Function Function&lt;User, ?&gt;} with any of the accepted types as return value</li>
     * </ul>
     *
     * @param priority the item priority. Higher priorities are displayed over lower priorities
     * @param slot     the slot to put the item in
     * @param item     the item to display
     * @return an {@link ItemReference} allowing modification of the item
     */
    @NotNull
    ItemReference setItem(int priority, int slot, @NotNull Object item);

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(int, int, Object)
     */
    @NotNull
    default ItemReference setItem(int priority, int slot, @NotNull Supplier<@NotNull ?> supplier) {
        return setItem(priority, slot, (Object) supplier);
    }

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(int, int, Object)
     */
    default ItemReference setItem(int priority, int slot, @NotNull Function<User, ?> itemFunction) {
        return setItem(priority, slot, (Object) itemFunction);
    }

    /**
     * Adds the template at the given priority
     *
     * @param priority the priority for the entire template
     * @param template the template with all the items
     */
    void setItems(int priority, @NotNull ItemTemplate template);

    /**
     * Opens the template as an inventory for a player.
     * <p>
     * Allowed types are:
     * <ul>
     * <li>{@link User} (will only work if online)</li>
     * <li>Platform-Specific Types</li>
     * </ul>
     *
     * @param player the player
     * @return the inventory that was opened, even if player is not online
     */
    @NotNull
    Inventory open(@NotNull Object player);
}
