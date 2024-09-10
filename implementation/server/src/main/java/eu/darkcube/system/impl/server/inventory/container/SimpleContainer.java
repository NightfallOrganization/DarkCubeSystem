/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.userapi.User;

public class SimpleContainer implements Container {
    protected final @NotNull List<ContainerListener> listeners = new CopyOnWriteArrayList<>();
    protected final @Nullable ItemBuilder @NotNull [] items;
    protected final List<Integer> itemSlots;

    public SimpleContainer(int size) {
        this.items = new ItemBuilder[size];
        this.itemSlots = IntStream.range(0, size).boxed().toList();
    }

    @Override
    public @Nullable ItemBuilder getAt(int slot) {
        var b = items[slot];
        return b == null ? null : b.clone();
    }

    @Override
    public void setAt(int slot, @Nullable ItemBuilder item) {
        var old = items[slot];
        if (item != null && item.material() == Material.air()) item = null;
        if (Objects.equals(item, old)) return;
        var slotItem = item == null ? null : item.clone();
        items[slot] = slotItem;
        var similar = old != null && slotItem != null && old.isSimilar(slotItem);
        var oldAmt = old == null ? 0 : old.amount();
        var newAmt = slotItem == null ? 0 : slotItem.amount();
        if (old == null || (similar && oldAmt < newAmt)) {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemAdded(slot, slotItem, newAmt - oldAmt);
            }
        } else if (slotItem == null || (similar && newAmt < oldAmt)) {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemRemoved(slot, old, oldAmt - newAmt);
            }
        } else {
            for (var i = 0; i < listeners.size(); i++) {
                listeners.get(i).onItemChanged(slot, old, slotItem);
            }
        }
    }

    @Override
    public void addListener(@NotNull ContainerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull ContainerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public @NotNull List<ContainerListener> listeners() {
        return List.copyOf(listeners);
    }

    @Override
    public boolean canPutItem(@NotNull User user, @NotNull ItemBuilder item, int slot, int putAmount) {
        return true;
    }

    @Override
    public boolean canTakeItem(@NotNull User user, int slot, int takeAmount) {
        return true;
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public @NotNull @Unmodifiable List<ItemBuilder> clearItemsOnClose(@NotNull User user) {
        var list = new ArrayList<ItemBuilder>();
        var items = this.items;
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (item == null) continue;
            var tryTakeAmount = item.amount();
            var takeAmount = Math.min(tryTakeAmount, getMaxTakeAmount(user, i, tryTakeAmount));
            if (takeAmount == 0) continue;
            if (!canTakeItem(user, i, takeAmount)) continue;
            var newItem = tryTakeAmount == takeAmount ? null : item.clone().amount(item.amount() - tryTakeAmount);
            var takeItem = item.clone().amount(takeAmount);
            setAt(i, newItem);
            list.add(takeItem);
        }
        return List.copyOf(list);
    }
}
