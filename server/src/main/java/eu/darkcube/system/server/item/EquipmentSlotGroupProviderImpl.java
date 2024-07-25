package eu.darkcube.system.server.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;

class EquipmentSlotGroupProviderImpl {
    private static final EquipmentSlotGroupProvider provider = InternalProvider.instance().instance(EquipmentSlotGroupProvider.class);

    static @NotNull EquipmentSlotGroup of(@NotNull Object platformObject) {
        return provider.of(platformObject);
    }
}
