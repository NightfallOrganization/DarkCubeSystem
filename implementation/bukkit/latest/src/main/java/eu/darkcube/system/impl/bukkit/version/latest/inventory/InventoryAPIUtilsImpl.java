/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import java.time.Duration;

import eu.darkcube.system.impl.bukkit.inventory.BukkitInventory;
import eu.darkcube.system.impl.bukkit.inventory.BukkitInventoryAPIUtils;
import eu.darkcube.system.impl.bukkit.inventory.BukkitTemplateInventory;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class InventoryAPIUtilsImpl extends BukkitInventoryAPIUtils {
    @Override
    public @NotNull Duration tickTime() {
        var rate = Bukkit.getServerTickManager().getTickRate();
        var time = 1000.0F / rate;
        return Duration.ofMillis((long) time);
    }

    @Override
    public InventoryCapabilities createCapabilities(BukkitInventory inventory) {
        if (inventory instanceof BukkitTemplateInventory template) {
            var view = template.inventoryView;
            return switch (view) {
                case CraftAnvilView anvil -> new AnvilCapabilities(inventory, anvil);
                case null, default -> super.createCapabilities(inventory);
            };
        }
        return super.createCapabilities(inventory);
    }

    @Override
    public InventoryView createAnvil(Player player, BukkitTemplateInventory bukkitTemplateInventory, @NotNull Component convert) {
        var craftPlayer = (CraftPlayer) player;
        var nmsPlayer = craftPlayer.getHandle();
        var containerId = nmsPlayer.nextContainerCounter();
        var menu = new AnvilContainer(containerId, nmsPlayer.getInventory(), convert);
        return menu.getBukkitView();
    }
}
