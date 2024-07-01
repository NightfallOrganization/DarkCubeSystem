/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import eu.darkcube.system.impl.server.util.ComputationUtil;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

class MinestomInventoryUtils {
    private static final ComputationUtil.Computer<User, ItemStack> ITEM_COMPUTER = ComputationUtil.createCommonItemComputer(ItemStack.class);
    private static final ComputationUtil.Computer<Void, Player> PLAYER_COMPUTER = ComputationUtil.createPlayerComputer(Player.class, user -> MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(user.uniqueId()));

    @Nullable
    static Player player(@Nullable Object player) {
        return PLAYER_COMPUTER.compute(player, null);
    }

    static @NotNull ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return ITEM_COMPUTER.compute(item, user);
    }
}
