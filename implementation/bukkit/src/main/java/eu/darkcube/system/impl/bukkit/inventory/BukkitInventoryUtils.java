package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.impl.server.util.ComputationUtil;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class BukkitInventoryUtils {
    private static final ComputationUtil.Computer<User, ItemStack> ITEM_COMPUTER = ComputationUtil.createCommonItemComputer(ItemStack.class);
    private static final ComputationUtil.Computer<Void, Player> PLAYER_COMPUTER = ComputationUtil.createPlayerComputer(Player.class, user -> Bukkit.getPlayer(user.uniqueId()));

    static @Nullable Player player(@Nullable Object player) {
        return PLAYER_COMPUTER.compute(player, null);
    }

    static @Nullable ItemStack computeItem(@Nullable User user, @Nullable Object item) {
        return ITEM_COMPUTER.compute(item, user);
    }
}
