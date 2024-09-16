/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.event.inventory.InventoryType;

public record ChestInventoryType(int size) implements BukkitInventoryType {
    @NotNull
    @Override
    public InventoryType bukkitType() {
        return InventoryType.CHEST;
    }

    @Override
    public @Nullable Component defaultTitle() {
        return eu.darkcube.system.server.inventory.InventoryType.of(bukkitType()).defaultTitle();
    }
}
