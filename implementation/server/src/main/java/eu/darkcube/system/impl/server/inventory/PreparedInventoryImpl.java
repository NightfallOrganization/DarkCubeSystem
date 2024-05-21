/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;

public abstract class PreparedInventoryImpl implements PreparedInventory {
    protected final Component title;
    protected final InventoryType type;
    protected final Map<Integer, Object> contents = new HashMap<>();

    public PreparedInventoryImpl(@NotNull Component title, InventoryType type) {
        this.title = title;
        this.type = type;
    }

    @Override
    public @NotNull Component title() {
        return title;
    }

    @Override
    public void setItem(int slot, @NotNull Object item) {
        contents.put(slot, item);
    }

    @Override
    public @NotNull Object getItem(int slot) {
        return contents.get(slot);
    }
}
