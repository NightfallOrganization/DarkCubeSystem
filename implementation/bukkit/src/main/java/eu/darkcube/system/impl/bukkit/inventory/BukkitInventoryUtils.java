/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.impl.server.util.ComputationUtil;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryUtils {
    private static final ComputationUtil.Computer<User, ItemStack> ITEM_COMPUTER = ComputationUtil.createCommonItemComputer(ItemStack.class);
    private static final ComputationUtil.Computer<Void, Player> PLAYER_COMPUTER = ComputationUtil.createPlayerComputer(Player.class, user -> Bukkit.getPlayer(user.uniqueId()));

    public static @Nullable Player player(@Nullable Object player) {
        return PLAYER_COMPUTER.compute(player, null);
    }

    public static @Nullable ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return ITEM_COMPUTER.compute(item, user);
    }
}
