/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.darkcube.system.impl.server.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;

public class StaticPagedInventoryContentProvider implements PagedInventoryContentProvider {
    private final List<ItemReferenceImpl> items = Collections.synchronizedList(new ArrayList<>());

    @Override
    public @NotNull BigInteger size() {
        return SIZE_UNKNOWN;
    }

    @Override
    public @NotNull ItemReference @NotNull [] provideItem(@NotNull BigInteger index, int length, @NotNull ItemReferenceProvider itemReferenceProvider) {
        return new ItemReference[0];
    }

    public ItemReferenceImpl addItem(Object item) {
        var reference = new ItemReferenceImpl(item);
        this.items.add(reference);
        return reference;
    }
}
