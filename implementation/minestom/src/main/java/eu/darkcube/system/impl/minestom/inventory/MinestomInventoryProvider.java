/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import static net.minestom.server.inventory.InventoryType.*;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;

public class MinestomInventoryProvider implements InventoryProvider {
    @Override
    public @NotNull InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType) {
        return new MinestomInventoryTemplate(key, inventoryType, ((MinestomInventoryType) inventoryType).minestomType().getSize());
    }

    @Override
    public @NotNull InventoryTemplate createChestTemplate(@NotNull Key key, int size) {
        return createTemplate(key, type(size));
    }

    @Override
    public @NotNull PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title) {
        return new MinestomPreparedInventory(title, inventoryType);
    }

    @Override
    public @NotNull PreparedInventory prepareChest(int size, @NotNull Component title) {
        return prepare(type(size), title);
    }

    private @NotNull InventoryType type(int size) {
        return switch (size) {
            case 9 -> InventoryType.of(CHEST_1_ROW);
            case 18 -> InventoryType.of(CHEST_2_ROW);
            case 27 -> InventoryType.of(CHEST_3_ROW);
            case 36 -> InventoryType.of(CHEST_4_ROW);
            case 45 -> InventoryType.of(CHEST_5_ROW);
            case 54 -> InventoryType.of(CHEST_6_ROW);
            default -> throw new IllegalArgumentException("Invalid size: " + size);
        };
    }
}
