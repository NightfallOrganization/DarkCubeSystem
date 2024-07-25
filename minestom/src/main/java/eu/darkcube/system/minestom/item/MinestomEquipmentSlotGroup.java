package eu.darkcube.system.minestom.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlotGroup;

public interface MinestomEquipmentSlotGroup extends EquipmentSlotGroup {
    @NotNull
    net.minestom.server.entity.EquipmentSlotGroup minestomType();
}
