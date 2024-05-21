/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;

public class ItemReferenceImpl implements ItemReference {

    private final @NotNull Object item;
    private boolean async = false;

    public ItemReferenceImpl(@NotNull Object item) {
        this.item = item;
    }

    public ItemReferenceImpl(@NotNull Object item, boolean async) {
        this.item = item;
        this.async = async;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public void makeAsync() {
        async = true;
    }

    @Override
    public void makeSync() {
        async = false;
    }

    public Object item() {
        return item;
    }

    @Override
    public ItemReferenceImpl clone() {
        return new ItemReferenceImpl(item, async);
    }

    @Override
    public String toString() {
        return "ItemReferenceImpl{" + "item=" + item + ", async=" + async + '}';
    }
}
