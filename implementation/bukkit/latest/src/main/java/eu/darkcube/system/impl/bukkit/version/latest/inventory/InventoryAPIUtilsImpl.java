/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import java.time.Duration;

import eu.darkcube.system.impl.bukkit.inventory.BukkitInventoryAPIUtils;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;

public class InventoryAPIUtilsImpl extends BukkitInventoryAPIUtils {
    @Override
    public @NotNull Duration tickTime() {
        var rate = Bukkit.getServerTickManager().getTickRate();
        var time = 1000.0F / rate;
        return Duration.ofMillis((long) time);
    }
}
