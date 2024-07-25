package eu.darkcube.system.server.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface EquipmentSlotGroupProvider {
    @NotNull
    EquipmentSlotGroup of(@NotNull Object platformObject);
}
