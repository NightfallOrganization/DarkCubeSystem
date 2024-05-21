/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import java.util.UUID;

import eu.darkcube.system.bukkit.item.attribute.BukkitAttributeModifier;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.EquipmentSlot;
import org.bukkit.attribute.AttributeModifier;

public record BukkitAttributeModifierImpl(AttributeModifier bukkitType) implements BukkitAttributeModifier {
    @Override public @Nullable EquipmentSlot equipmentSlot() {
        var slot = bukkitType.getSlot();
        if (slot == null) return null;
        return EquipmentSlot.of(slot);
    }

    @Override public @NotNull UUID uniqueId() {
        return bukkitType.getUniqueId();
    }
}
