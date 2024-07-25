package eu.darkcube.system.server.item;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public interface EquipmentSlotGroup {
    @NotNull
    @Unmodifiable
    List<EquipmentSlot> slots();

    static @NotNull EquipmentSlotGroup of(@NotNull Object platformObject) {
        return EquipmentSlotGroupProviderImpl.of(platformObject);
    }
}
