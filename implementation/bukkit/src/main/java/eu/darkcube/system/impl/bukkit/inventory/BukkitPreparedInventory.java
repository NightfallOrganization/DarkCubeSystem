package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.server.inventory.PreparedInventoryImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.userapi.UserAPI;

public class BukkitPreparedInventory extends PreparedInventoryImpl {
    public BukkitPreparedInventory(@NotNull Component title, InventoryType type) {
        super(title, type);
    }

    @Override
    public @NotNull Inventory open(@NotNull Object player) {
        var inventory = new BukkitInventory(title, (BukkitInventoryType) type);
        var p = BukkitInventoryUtils.player(player);
        if (p == null) throw new IllegalArgumentException("Player " + player + " not found");
        var user = UserAPI.instance().user(p.getUniqueId());
        for (var entry : contents.entrySet()) {
            var slot = entry.getKey();
            var item = entry.getValue();
            var stack = BukkitInventoryUtils.computeItem(user, item);
            if (stack != null) {
                inventory.setItem(slot, stack);
            }
        }
        inventory.open(p);
        return inventory;
    }
}
