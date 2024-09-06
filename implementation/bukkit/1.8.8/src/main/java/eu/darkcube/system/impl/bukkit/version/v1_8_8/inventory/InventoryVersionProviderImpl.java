/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory;

import eu.darkcube.system.impl.bukkit.inventory.BukkitTemplateInventory;
import eu.darkcube.system.impl.bukkit.inventory.InventoryVersionProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryVersionProviderImpl implements InventoryVersionProvider {
    @Override
    public @Nullable Object tryConvertTitle(@NotNull Object title) {
        return null;
    }

    @Override
    public boolean handleCustomClickTop(BukkitTemplateInventory inventory, InventoryClickEvent event) {
        return false;
    }

    @Override
    public boolean handleCustomClickBottom(BukkitTemplateInventory inventory, InventoryClickEvent event) {
        return false;
    }
}
