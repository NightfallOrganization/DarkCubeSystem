package eu.darkcube.system.impl.minestom.item;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.EquipmentSlotGroupProvider;

public class MinestomEquipmentSlotGroupProvider implements EquipmentSlotGroupProvider {
    private final EquipmentSlotGroup[] groups = EnumConverter.convert(net.minestom.server.entity.EquipmentSlotGroup.class, EquipmentSlotGroup.class, MinestomEquipmentSlotGroupImpl::new);

    @Override
    public @NotNull EquipmentSlotGroup of(@NotNull Object platformObject) {
        if (platformObject instanceof EquipmentSlotGroup group) return group;
        if (platformObject instanceof net.minestom.server.entity.EquipmentSlotGroup group) return groups[group.ordinal()];
        throw new IllegalArgumentException("Invalid EquipmentSlotGroup: " + platformObject);
    }
}
