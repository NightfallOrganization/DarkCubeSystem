/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.userapi.User;

public interface PageButton {
    int @NotNull [] slots();

    void slots(int @NotNull [] slots);

    @NotNull
    ItemReference setItem(@NotNull Object item);

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(Object)
     */
    @NotNull
    default ItemReference setItem(@NotNull Supplier<@NotNull ?> itemSupplier) {
        return setItem((Object) itemSupplier);
    }

    /**
     * Utility method to allow lamdas
     *
     * @see #setItem(Object)
     */
    @NotNull
    default ItemReference setItem(@NotNull Function<@NotNull User, @NotNull ?> itemFunction) {
        return setItem((Object) itemFunction);
    }

    @NotNull
    Visibility visibility();

    void visibility(@NotNull Visibility visibility);

    enum Visibility {
        ALWAYS,
        WHEN_USABLE
    }
}
