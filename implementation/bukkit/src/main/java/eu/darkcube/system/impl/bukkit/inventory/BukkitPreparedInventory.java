package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.impl.server.inventory.PreparedInventoryImpl;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;

public class BukkitPreparedInventory extends PreparedInventoryImpl {
    public BukkitPreparedInventory(@NotNull Component title, InventoryType type) {
        super(title, type);
    }

    @Override
    public @NotNull Inventory open(@NotNull Object player) {
        var inventory = new BukkitInventory(title, (BukkitInventoryType) type);
        inventory.open(player);
        return inventory;
    }
}
