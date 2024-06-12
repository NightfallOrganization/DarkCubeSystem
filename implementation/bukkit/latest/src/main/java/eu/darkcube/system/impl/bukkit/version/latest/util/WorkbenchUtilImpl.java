/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.util;

import eu.darkcube.system.bukkit.util.WorkbenchUtil;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class WorkbenchUtilImpl implements WorkbenchUtil {

    @Override public InventoryView openWorkbench(Player player, Location location, boolean force, Component title) {
        var cp = (CraftPlayer) player;

        var pos = CraftLocation.toBlockPosition(location);
        var level = cp.getHandle().level();
        MenuProvider provider = new SimpleMenuProvider((syncId, inventory, p1) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(level, pos)), net.minecraft.network.chat.Component.literal(LegacyComponentSerializer
                .legacySection()
                .serialize(title)));

        cp.getHandle().openMenu(provider);
        if (force) cp.getHandle().containerMenu.checkReachable = false;
        return cp.getHandle().containerMenu.getBukkitView();
    }
}
