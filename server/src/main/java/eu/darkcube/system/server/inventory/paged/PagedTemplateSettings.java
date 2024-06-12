/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.util.Map;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.InventoryTemplateSettings;

@Api
public interface PagedTemplateSettings extends InventoryTemplateSettings, Cloneable {
    @Api
    @NotNull
    PagedTemplateSettings clone();

    @Api
    @NotNull
    PagedInventoryContent content();

    /**
     * Get a copy of the pageSlots. These slots will be used for paged items.
     * <p>
     * See {@link #pageSlots(int[])} for order of the array
     *
     * @return the slots where paged items will be
     * @see #pageSlots(int[])
     */
    @Api
    int @NotNull [] pageSlots();

    /**
     * Set the pageSlots for this template.
     * <p>
     * The slots first in this array will be filled first by default.
     * Use {@link #sorter(PageSlotSorter)} to use a custom sorter.
     *
     * @param pageSlots the pageSlots to use
     */
    @Api
    void pageSlots(int @NotNull [] pageSlots);

    /**
     * Gets the sorter for this inventory.
     * The sorter has no impact on special pageSlots defined via {@link #specialPageSlots(int[])}.
     * These special slots must already be in the correct order.
     *
     * @return the sorter used to sort displayed items
     */
    @Api
    @NotNull
    PageSlotSorter sorter();

    /**
     * Changes the {@link PageSlotSorter}.
     * The sorter has no impact on special pageSlots defined via {@link #specialPageSlots(int[])}.
     * These special slots must already be in the correct order.
     *
     * @param sorter the sorter used to sort displayed items
     */
    @Api
    void sorter(@NotNull PageSlotSorter sorter);

    /**
     * Adds a special configuration for page slots.
     * If the page has exactly {@code pageSlots.length} slots, this configuration will be used.
     * This overrides an old special configuration with the same size.
     *
     * @param pageSlots the pageSlots to use
     */
    @Api
    void specialPageSlots(int @NotNull [] pageSlots);

    /**
     * Removes a special configuration for page slots.
     * Takes the size of the existing pageSlots array as input.
     *
     * @param size the size to remove
     * @return whether the template was modified
     */
    @Api
    boolean removeSpecialPageSlots(int size);

    /**
     * Get an unmodifiable map of the existing special pageSlots configurations.
     *
     * @return the special pageSlots configurations
     */
    @Api
    @NotNull
    @Unmodifiable
    Map<Integer, int[]> specialPageSlots();

    @Api
    @NotNull
    PageButton nextButton();

    @Api
    @NotNull
    PageButton previousButton();
}
