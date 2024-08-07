package eu.darkcube.system.impl.bukkit.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryType;

public record ChestInventoryType(int size) implements BukkitInventoryType {
    @NotNull
    @Override
    public InventoryType bukkitType() {
        return InventoryType.CHEST;
    }
}
