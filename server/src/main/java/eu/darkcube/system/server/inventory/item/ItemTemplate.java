/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.item;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.userapi.User;

/**
 * A template for multiple items.
 * These items might server as background (glass panes) or anything else.
 * <p>
 * Keep in mind than an {@link ItemTemplate} can not be used safely for differently sized inventories,
 * because the slots may differ. This is not forced though.
 */
public interface ItemTemplate {
    static @NotNull ItemTemplate create() {
        return ItemTemplateProviderImpl.itemTemplateProvider().create();
    }

    /**
     * Set an item at every slot in the specified array
     *
     * @param item  the item to set, see {@link InventoryTemplate#setItem(int, int, Object)} for allowed types
     * @param slots the slots to set the item at
     * @return an {@link ItemReference} bundling all the slots
     */
    @NotNull
    ItemReference setItem(@NotNull Object item, int... slots);

    /**
     * Set an item at the specified slot
     *
     * @param item the item to set, see {@link InventoryTemplate#setItem(int, int, Object)} for allowed types
     * @param slot the slot to set the item at
     * @return an {@link ItemReference} for the item
     */
    default ItemReference setItem(int slot, @NotNull Object item) {
        return setItem(item, slot);
    }

    /**
     * Utility method to allow lambdas
     *
     * @see #setItem(int, Object)
     */
    default ItemReference setItem(int slot, @NotNull Supplier<@NotNull ?> supplier) {
        return setItem(slot, (Object) supplier);
    }

    /**
     * Utility method to allow lambdas
     *
     * @see #setItem(int, Object)
     */
    default ItemReference setItem(int slot, @NotNull Function<@NotNull User, @NotNull ?> itemFunction) {
        return setItem(slot, (Object) itemFunction);
    }

    /**
     * Gets a copy of the contents of this inventory.
     * Map values are same as {@link InventoryTemplate#setItem(int, int, Object)}
     *
     * @return a map with the contents
     */
    @NotNull
    @Unmodifiable
    Map<Integer, ? extends ItemReference> contents();
}
