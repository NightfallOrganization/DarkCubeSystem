/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;

public class AnvilContainer extends AnvilMenu {
    public AnvilContainer(int containerId, Inventory inventory, Component title) {
        super(containerId, inventory);
        checkReachable = false;
        setTitle(PaperAdventure.asVanilla(title));
    }

    @Override
    public void createResult() {
        var output = this.getSlot(2);
        if (!output.hasItem()) {
            output.set(this.getSlot(0).getItem().copy());
        }

        this.cost.set(0);

        this.sendAllDataToRemote();
        this.broadcastChanges();
    }

    @Override
    public void removed(@NotNull Player player) {
    }

    @Override
    protected void clearContainer(@NotNull Player player, @NotNull Container inventory) {
    }
}
