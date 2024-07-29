/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import java.util.function.Supplier;

import eu.darkcube.system.impl.server.inventory.LazyInventoryTemplate;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;

public class BukkitInventoryProviderImpl implements InventoryProvider {
    @Override
    public @NotNull InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType) {
        return null;
    }

    @Override
    public @NotNull InventoryTemplate createChestTemplate(@NotNull Key key, int size) {
        return null;
    }

    @Override
    public @NotNull PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title) {
        return null;
    }

    @Override
    public @NotNull PreparedInventory prepareChest(int size, @NotNull Component title) {
        return null;
    }

    @Override
    public @NotNull InventoryTemplate lazy(@NotNull Supplier<@NotNull InventoryTemplate> supplier) {
        return new LazyInventoryTemplate(supplier);
    }
}
