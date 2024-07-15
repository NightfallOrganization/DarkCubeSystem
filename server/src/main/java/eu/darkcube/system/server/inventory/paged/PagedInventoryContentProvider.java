/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.math.BigInteger;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;

public interface PagedInventoryContentProvider {
    @NotNull
    BigInteger SIZE_UNKNOWN = BigInteger.valueOf(-1L);

    /**
     * @return the total size of the content, {@link #SIZE_UNKNOWN} if unknown
     */
    @NotNull
    BigInteger size();

    /**
     * Provides the items for a given index and length
     * The length parameter may exceed the available items.
     * In that case all remaining items should be returned.
     * <p>
     * Allowed types in Array:
     * <ul>
     *     <li>All types from {@link InventoryTemplate#setItem(int, int, Object)}</li>
     * </ul>
     *
     * @param index  the index of the first item
     * @param length the length of the requested sequence
     * @return an array containing all items. The array size may differ from the input {@code length}
     */
    @NotNull
    ItemReference @NotNull [] provideItem(@NotNull BigInteger index, int length, @NotNull ItemReferenceProvider itemReferenceProvider);
}
