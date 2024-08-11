/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BukkitInventoryTemplatePlatformProvider implements DarkCubeInventoryTemplates.PlatformProvider {
    @Override
    public @NotNull ItemBuilder previousItem() {
        return ItemBuilder.item(Material.STAINED_GLASS_PANE).damage(5);
    }

    @Override
    public @NotNull ItemBuilder nextItem() {
        return ItemBuilder.item(Material.STAINED_GLASS_PANE).damage(5);
    }

    @Override
    public void playSound(@NotNull User user) {
        var player = Bukkit.getPlayer(user.uniqueId());
        if (player == null) return;
        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 100, 1);
    }
}
