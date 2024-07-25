package eu.darkcube.system.impl.bukkit.item;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bukkit.item.BukkitEquipmentSlotGroup;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings({"UnstableTypeUsedInSignature", "UnstableApiUsage"})
public record BukkitEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup bukkitType, @NotNull @Unmodifiable List<EquipmentSlot> slots) implements BukkitEquipmentSlotGroup {
    public BukkitEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup bukkitType) {
        var slots = new ArrayList<EquipmentSlot>();
        for (var slot : org.bukkit.inventory.EquipmentSlot.values()) {
            if (bukkitType.test(slot)) {
                slots.add(EquipmentSlot.of(slot));
            }
        }
        this(bukkitType, List.copyOf(slots));
    }
}
