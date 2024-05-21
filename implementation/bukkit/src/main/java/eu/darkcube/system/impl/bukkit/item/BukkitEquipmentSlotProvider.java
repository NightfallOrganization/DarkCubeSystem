/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlot;
import eu.darkcube.system.server.item.EquipmentSlotProvider;

public class BukkitEquipmentSlotProvider implements EquipmentSlotProvider {
    private final EquipmentSlot[] slots = EnumConverter.convert(org.bukkit.inventory.EquipmentSlot.class, EquipmentSlot.class, BukkitEquipmentSlot::new);

    @Override public @NotNull EquipmentSlot of(@NotNull Object platformObject) {
        if (platformObject instanceof EquipmentSlot slot) return slot;
        if (platformObject instanceof org.bukkit.inventory.EquipmentSlot slot) return slots[slot.ordinal()];
        throw new IllegalArgumentException("Invalid EquipmentSlot: " + platformObject);
    }
}
