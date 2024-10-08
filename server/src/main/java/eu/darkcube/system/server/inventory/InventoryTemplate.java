/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;
import eu.darkcube.system.server.inventory.container.ContainerViewFactory;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
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
@Api
public interface InventoryTemplate extends Keyed {
    @Api
    @NotNull
    static InventoryTemplate lazy(@NotNull Supplier<@NotNull InventoryTemplate> supplier) {
        return InventoryProviderImpl.inventoryProvider().lazy(supplier);
    }

    /**
     * Gets the {@link InventoryType} of this inventory.
     * Inventories can have different layouts, like Anvils, Chests, Furnaces, ...
     * These layouts are called {@link InventoryType InventoryTypes}
     *
     * @return this inventory's type
     */
    @Api
    @NotNull
    InventoryType type();

    int size();

    @Api
    void addContainerFactory(@NotNull ContainerViewFactory factory);

    @Api
    void removeContainerFactory(@NotNull ContainerViewFactory factory);

    @Api
    @NotNull
    @Unmodifiable
    List<@NotNull ContainerViewFactory> containerFactories();

    /**
     * Gets the {@link AnimatedTemplateSettings} for this inventory.
     * This allows configuration for animations.
     *
     * @return the animation settings
     */
    @Api
    @NotNull
    AnimatedTemplateSettings animation();

    /**
     * Gets the {@link PagedTemplateSettings} for this inventory.
     * This allows configuration of an inventory with multiple pages if the items may
     * not fit into a single inventory.
     *
     * @return the pagination settings
     */
    @Api
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
    @Api
    void title(@Nullable Object title);

    /**
     * Gets a copy of all the listener
     *
     * @return Collection containing the listeners
     */
    @Api
    @Unmodifiable
    @NotNull
    Collection<InventoryListener> listeners();

    /**
     * Adds an {@link InventoryListener} to this template
     *
     * @param listener the listener to add
     */
    @Api
    void addListener(@NotNull InventoryListener listener);

    /**
     * Removes an {@link InventoryListener} from this template
     *
     * @param listener the listener to remove
     */
    @Api
    void removeListener(@NotNull InventoryListener listener);

    /**
     * Adds an {@link TemplateInventoryListener} to this template
     *
     * @param listener the listener to add
     */
    @Api
    void addListener(@NotNull TemplateInventoryListener listener);

    /**
     * Removes an {@link TemplateInventoryListener} from this template
     *
     * @param listener the listener to remove
     */
    @Api
    void removeListener(@NotNull TemplateInventoryListener listener);

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
     * @param priority the item priority. Higher priorities are displayed over lower priorities. Avoid value {@value PagedInventoryContent#PRIORITY}.
     * @param slot     the slot to put the item in
     * @param item     the item to display
     * @return an {@link ItemReference} allowing modification of the item
     */
    @Api
    @NotNull
    ItemReference setItem(int priority, int slot, @Nullable Object item);

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(int, int, Object)
     */
    @Api
    @NotNull
    default ItemReference setItem(int priority, int slot, @NotNull Supplier<@Nullable ?> supplier) {
        return setItem(priority, slot, (Object) supplier);
    }

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(int, int, Object)
     */
    @Api
    default ItemReference setItem(int priority, int slot, @NotNull Function<@NotNull User, ?> itemFunction) {
        return setItem(priority, slot, (Object) itemFunction);
    }

    /**
     * Adds the template at the given priority
     *
     * @param priority the priority for the entire template. Avoid value {@value PagedInventoryContent#PRIORITY}.
     * @param template the template with all the items
     */
    @Api
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
    @Api
    @NotNull
    Inventory open(@NotNull Object player);
}
