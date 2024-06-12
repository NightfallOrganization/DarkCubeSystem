/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.userapi.User;

public interface PagedInventoryContent {
    /**
     * Adds static items to the inventory. This can only be used if no custom provider is in use.
     * Otherwise this throws an {@link IllegalStateException}.
     * <p>
     * Usage of this is recommended when the contents can't change after the inventory is opened.
     * If the contents do change, use a custom provider ({@link #provider(PagedInventoryContentProvider)})
     * <p>
     * The {@code publishUpdate...} methods will work with this, and can be used to recompute items.
     * This is not recommended, because inserted items would have to be tracked by the user (you!).
     * The only way to use dynamic items with this method safely is to use {@link #publishUpdateAll()},
     * which is not very efficient.
     * <p>
     * Allowed types:
     * <ul>
     *     <li>All types allowed by {@link InventoryTemplate#setItem(int, int, Object)}</li>
     * </ul>
     *
     * @param item the item(s) to add
     * @return an item reference
     */
    @Api
    @NotNull
    ItemReference addStaticItem(@NotNull Object item);

    /**
     * Utility method to allow lambdas
     *
     * @see #addStaticItem(Object)
     */
    @Api
    @NotNull
    default ItemReference addStaticItem(@NotNull Supplier<@NotNull ?> itemSupplier) {
        return addStaticItem((Object) itemSupplier);
    }

    /**
     * Utility method to allow lambdas
     *
     * @see #addStaticItem(Object)
     */
    @Api
    @NotNull
    default ItemReference addStaticItem(@NotNull Function<@NotNull User, @NotNull ?> itemFunction) {
        return addStaticItem((Object) itemFunction);
    }

    /**
     * Gets current content provider. By default, this is a simple static implementation.
     * This can be changed to a custom provider with {@link #provider(PagedInventoryContentProvider)} to allow for more control
     *
     * @return the current content provider
     */
    @Api
    @NotNull
    PagedInventoryContentProvider provider();

    /**
     * Allows setting a custom content provider. This allows for more dynamic items and more control.
     * Use the {@code #publishUpdate...} methods to update the inventory in case the content has changed.
     *
     * @param provider the custom provider
     */
    @Api
    void provider(@NotNull PagedInventoryContentProvider provider);

    /**
     * Updates the item at the given index.
     * In case of removal, use {@link #publishUpdateRemoveAt(int)}
     * In case of addition, use {@link #publishUpdateInsertAfter(int)} or {@link #publishUpdateInsertAfter(int)}
     *
     * @param index the index that was updated
     */
    @Api
    void publishUpdate(int index);

    /**
     * Updates the entire page.
     */
    @Api
    void publishUpdatePage();

    /**
     * Updates all items.
     */
    @Api
    void publishUpdateAll();

    /**
     * Publishes that the size has changed.
     * A size change implies added or removed items. The corresponding update methods should be used.
     * If the entire (or most of) the inventory has changed, {@link #publishUpdateAll()} should be used.
     * <p>
     * The default implementation is the same as {@link #publishUpdateAll()}.
     *
     * @deprecated This is deprecated and mostly used as a reference for extra documentation.
     */
    @Deprecated
    @Api
    default void publishUpdateSize() {
        publishUpdateAll();
    }

    /**
     * Updates the index to be removed.
     * In case of removal, use this rather than {@link #publishUpdate(int)} to also update all following items.
     *
     * @param index the index that was removed
     */
    @Api
    void publishUpdateRemoveAt(int index);

    /**
     * Updates the index to have an item inserted before it.
     *
     * @param index the index as a reference
     */
    @Api
    void publishUpdateInsertBefore(int index);

    /**
     * Updates the index to have an item inserted after it.
     *
     * @param index the index as a reference
     */
    @Api
    void publishUpdateInsertAfter(int index);
}
