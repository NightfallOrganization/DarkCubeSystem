/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public abstract class AbstractInventory<PlatformItemStack> implements Inventory {
    protected final @Nullable Component title;
    protected final @NotNull InventoryType type;
    protected final int size;
    protected final @NotNull List<@NotNull User> opened = new CopyOnWriteArrayList<>();
    protected final @NotNull List<@NotNull InventoryListener> listeners = new CopyOnWriteArrayList<>();

    public AbstractInventory(@Nullable Component title, @NotNull InventoryType type, int size) {
        this.title = title;
        this.type = type;
        this.size = size;
    }

    @Override
    public @Nullable Component title() {
        return title;
    }

    @Override
    public void setItem(int slot, @NotNull Object item) {
        setItem0(slot, retrieveItem(item));
    }

    @NotNull
    @Override
    public InventoryType type() {
        return type;
    }

    @Override
    public int size() {
        return size;
    }

    protected PlatformItemStack retrieveItem(@NotNull Object item) {
        while (true) {
            if (item instanceof Supplier<?> supplier) {
                item = supplier.get();
            } else if (item instanceof ItemBuilder builder) {
                item = builder.buildSafe();
            } else {
                break;
            }
        }
        return (PlatformItemStack) item;
    }

    protected abstract void setItem0(int slot, @NotNull PlatformItemStack item);

    protected abstract PlatformItemStack getItem0(int slot);

    @Override
    public @NotNull ItemBuilder getItem(int slot) {
        return ItemBuilder.item(getItem0(slot));
    }

    @Override
    public @Unmodifiable @NotNull Collection<@NotNull InventoryListener> listeners() {
        return List.copyOf(listeners);
    }

    @Override
    public void addListener(@NotNull InventoryListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull InventoryListener listener) {
        this.listeners.remove(listener);
    }

    @NotNull
    @Override
    public List<User> opened() {
        return List.copyOf(opened);
    }
}
