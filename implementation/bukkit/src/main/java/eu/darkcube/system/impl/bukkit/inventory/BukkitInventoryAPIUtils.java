/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import java.time.Duration;

import eu.darkcube.system.impl.server.inventory.InventoryAPIUtils;
import eu.darkcube.system.impl.server.inventory.InventoryCapabilitiesImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class BukkitInventoryAPIUtils implements InventoryAPIUtils {
    @Override
    public @NotNull Duration tickTime() {
        return defaultTickTime();
    }

    public InventoryCapabilities createCapabilities(BukkitInventory inventory) {
        return InventoryCapabilitiesImpl.NO_CAPABILITIES;
    }

    public InventoryView createAnvil(Player player, BukkitTemplateInventory bukkitTemplateInventory, @NotNull Component convert) {
        throw new UnsupportedOperationException();
    }

    static BukkitInventoryAPIUtils utils() {
        return (BukkitInventoryAPIUtils) InventoryAPIUtils.utils();
    }

}
