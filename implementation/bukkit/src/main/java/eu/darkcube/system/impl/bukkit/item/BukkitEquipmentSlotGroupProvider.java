package eu.darkcube.system.impl.bukkit.item;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.EquipmentSlotGroupProvider;

public class BukkitEquipmentSlotGroupProvider implements EquipmentSlotGroupProvider {
    private final Map<org.bukkit.inventory.EquipmentSlotGroup, EquipmentSlotGroup> map = new HashMap<>();

    private void put(org.bukkit.inventory.EquipmentSlotGroup group) {
        map.put(group, new BukkitEquipmentSlotGroupImpl(group));
    }

    @Override
    public @NotNull EquipmentSlotGroup of(@NotNull Object platformObject) {
        if (platformObject instanceof EquipmentSlotGroup group) return group;
        if (platformObject instanceof org.bukkit.inventory.EquipmentSlotGroup group) return map.computeIfAbsent(group, BukkitEquipmentSlotGroupImpl::new);
        throw new IllegalArgumentException("Invalid EquipmentSlotGroup: " + platformObject);
    }
}
