/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.provider.InternalProvider;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryVersionProvider {
    @Nullable
    Object tryConvertTitle(@NotNull Object title);

    boolean handleCustomClickTop(BukkitTemplateInventory inventory, InventoryClickEvent event);

    boolean handleCustomClickBottom(BukkitTemplateInventory inventory, InventoryClickEvent event);

}

class InventoryVersionProviderImpl {
    static final InventoryVersionProvider provider = InternalProvider.instance().instance(InventoryVersionProvider.class);
}