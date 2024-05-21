/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import eu.darkcube.system.impl.server.inventory.PreparedInventoryImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;

public class MinestomPreparedInventory extends PreparedInventoryImpl {
    public MinestomPreparedInventory(@NotNull Component title, InventoryType type) {
        super(title, type);
    }

    @Override
    public @NotNull Inventory open(@NotNull Object player) {
        var inventory = new MinestomInventory(title, (MinestomInventoryType) type);
        inventory.open(player);
        return inventory;
    }
}
