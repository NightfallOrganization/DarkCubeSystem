package eu.darkcube.system.impl.minestom.item;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.minestom.item.MinestomEquipmentSlotGroup;
import eu.darkcube.system.server.item.EquipmentSlot;
import net.minestom.server.entity.EquipmentSlotGroup;

public record MinestomEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup minestomType, @NotNull @Unmodifiable List<EquipmentSlot> slots) implements MinestomEquipmentSlotGroup {
    public MinestomEquipmentSlotGroupImpl(@NotNull EquipmentSlotGroup minestomType) {
        this(minestomType, minestomType.equipmentSlots().stream().map(EquipmentSlot::of).toList());
    }
}
