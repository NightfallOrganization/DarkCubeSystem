/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.Material;

public class MinestomInventoryTemplatePlatformProvider implements DarkCubeInventoryTemplates.PlatformProvider {
    @Override
    public @NotNull ItemBuilder previousItem() {
        return ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE);
    }

    @Override
    public @NotNull ItemBuilder nextItem() {
        return ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE);
    }

    @Override
    public void playSound(@NotNull User user) {
        var player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(user.uniqueId());
        if (player == null) return;
        player.playSound(Sound.sound(Key.key("block.note_block.hat"), Sound.Source.MASTER, 100, 1));
    }
}
