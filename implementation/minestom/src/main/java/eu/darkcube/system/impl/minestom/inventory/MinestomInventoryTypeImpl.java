/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import net.minestom.server.inventory.InventoryType;

public record MinestomInventoryTypeImpl(@NotNull InventoryType minestomType) implements MinestomInventoryType {
    @Override
    public int size() {
        return minestomType.getSize();
    }
}
