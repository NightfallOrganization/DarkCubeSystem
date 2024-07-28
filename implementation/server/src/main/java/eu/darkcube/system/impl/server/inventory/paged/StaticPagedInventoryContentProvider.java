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

public final class StaticPagedInventoryContentProvider implements PagedInventoryContentProvider, Cloneable {
    private final List<ItemReferenceImpl> items = Collections.synchronizedList(new ArrayList<>());

    public StaticPagedInventoryContentProvider() {
    }

    public StaticPagedInventoryContentProvider(List<ItemReferenceImpl> items) {
        this();
        this.items.addAll(items);
    }

    @Override
    public @NotNull BigInteger size() {
        return BigInteger.valueOf(items.size());
    }

    @Override
    public @NotNull ItemReference @NotNull [] provideItem(@NotNull BigInteger index, int length, @NotNull ItemReferenceProvider itemReferenceProvider) {
        var start = index.intValueExact();
        var list = new ArrayList<ItemReference>();
        for (var i = 0; i < length && start + i < items.size(); i++) {
            list.add(items.get(start + i));
        }
        return list.toArray(ItemReference[]::new);
    }

    public ItemReferenceImpl addItem(Object item) {
        var reference = new ItemReferenceImpl(item);
        this.items.add(reference);
        return reference;
    }

    public void removeItem(ItemReferenceImpl item) {
        items.remove(item);
    }

    public List<ItemReferenceImpl> items() {
        return items;
    }

    @Override
    protected StaticPagedInventoryContentProvider clone() {
        return new StaticPagedInventoryContentProvider(this.items);
    }
}
