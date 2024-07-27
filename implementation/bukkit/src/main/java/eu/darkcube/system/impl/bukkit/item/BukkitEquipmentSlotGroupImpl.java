package eu.darkcube.system.impl.bukkit.item;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bukkit.item.BukkitEquipmentSlotGroup;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings({"UnstableTypeUsedInSignature", "UnstableApiUsage"})
public record BukkitEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup bukkitType,
                                           @NotNull @Unmodifiable List<EquipmentSlot> slots) implements BukkitEquipmentSlotGroup {
    public BukkitEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup bukkitType) {
        this(bukkitType, List.copyOf(computeSlots(bukkitType)));
    }

    /**
     * Utility method to avoid statements before super.
     * Checkstyle seems to have a problem with that
     */
    private static List<EquipmentSlot> computeSlots(EquipmentSlotGroup bukkitType) {
        var slots = new ArrayList<EquipmentSlot>();
        for (var slot : org.bukkit.inventory.EquipmentSlot.values()) {
            if (bukkitType.test(slot)) {
                slots.add(EquipmentSlot.of(slot));
            }
        }
        return slots;
    }
}
