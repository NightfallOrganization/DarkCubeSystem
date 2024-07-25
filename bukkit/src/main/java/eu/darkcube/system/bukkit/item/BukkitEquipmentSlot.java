package eu.darkcube.system.bukkit.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlot;

public interface BukkitEquipmentSlot extends EquipmentSlot {
    @NotNull
    org.bukkit.inventory.EquipmentSlot bukkitType();
}
