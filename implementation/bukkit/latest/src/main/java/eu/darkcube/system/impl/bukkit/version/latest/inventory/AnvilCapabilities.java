/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.inventory;

import eu.darkcube.system.impl.bukkit.inventory.BukkitInventory;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;

public class AnvilCapabilities implements InventoryCapabilities.Anvil {
    private final BukkitInventory inventory;
    private final CraftAnvilView anvil;

    public AnvilCapabilities(BukkitInventory inventory, CraftAnvilView anvil) {
        this.inventory = inventory;
        this.anvil = anvil;
    }

    @Override
    public @Nullable String renameText() {
        return anvil.getRenameText();
    }
}
