/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.util;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public interface WorkbenchUtil {

    InventoryView openWorkbench(Player player, Location location, boolean force, Component title);

    default InventoryView openWorkbench(Player player, Component title) {
        return openWorkbench(player, player.getLocation(), true, title);
    }
}
