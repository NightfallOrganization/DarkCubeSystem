/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryTemplateSettings;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.userapi.User;

public interface PagedTemplateSettings extends InventoryTemplateSettings {
    /**
     * Adds an item to the page.
     * <p>
     * Allowed Types:
     * <ul>
     *     <li>{@link InventoryTemplate#setItem(int, int, Object)}</li>
     *     <li>{@link Collection} of any allowed type</li>
     *     <li>{@link Array} of any allowed type</li>
     * </ul>
     *
     * @param item the item to add.
     * @return an {@link ItemReference} to allow more configuration
     */
    @NotNull
    ItemReference addItem(@NotNull Object item);

    /**
     * Utility method to allow lamdas
     *
     * @see #addItem(Object)
     */
    @NotNull
    default ItemReference addItem(@NotNull Supplier<@NotNull ?> itemSupplier) {
        return addItem((Object) itemSupplier);
    }

    /**
     * Utility method to allow lamdas
     *
     * @see #addItem(Object)
     */
    @NotNull
    default ItemReference addItem(@NotNull Function<@NotNull User, @NotNull ?> itemFunction) {
        return addItem((Object) itemFunction);
    }

    /**
     * Get a copy of the pageSlots. These slots will be used for paged items added with {@link #addItem(Object)}.
     * <p>
     * See {@link #pageSlots(int[])} for order of the array
     *
     * @return the slots where paged items will be
     * @see #pageSlots(int[])
     */
    int @NotNull [] pageSlots();

    /**
     * Set the pageSlots for this template.
     * <p>
     * The slots first in this array will be filled first by default.
     * Use {@link #sorter(PageSlotSorter)} to use a custom sorter.
     *
     * @param pageSlots the pageSlots to use
     */
    void pageSlots(int @NotNull [] pageSlots);

    /**
     * Gets the sorter for this inventory.
     * The sorter has no impact on special pageSlots defined via {@link #specialPageSlots(int[])}.
     * These special slots must already be in the correct order.
     *
     * @return the sorter used to sort displayed items
     */
    @NotNull
    PageSlotSorter sorter();

    /**
     * Changes the {@link PageSlotSorter}.
     * The sorter has no impact on special pageSlots defined via {@link #specialPageSlots(int[])}.
     * These special slots must already be in the correct order.
     *
     * @param sorter the sorter used to sort displayed items
     */
    void sorter(@NotNull PageSlotSorter sorter);

    /**
     * Adds a special configuration for page slots.
     * If the page has exactly {@code pageSlots.length} slots, this configuration will be used.
     * This overrides an old special configuration with the same size.
     *
     * @param pageSlots the pageSlots to use
     */
    void specialPageSlots(int @NotNull [] pageSlots);

    /**
     * Removes a special configuration for page slots.
     * Takes the size of the existing pageSlots array as input.
     *
     * @param size the size to remove
     * @return whether the template was modified
     */
    boolean removeSpecialPageSlots(int size);

    /**
     * Get an unmodifiable map of the existing special pageSlots configurations.
     *
     * @return the special pageSlots configurations
     */
    @NotNull
    @Unmodifiable
    Map<Integer, int[]> specialPageSlots();

    @NotNull
    PageButton nextButton();

    @NotNull
    PageButton previousButton();
}
