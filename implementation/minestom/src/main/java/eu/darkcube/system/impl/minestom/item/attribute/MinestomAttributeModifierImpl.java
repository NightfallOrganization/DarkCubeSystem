/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.attribute;

import java.util.UUID;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifier;
import eu.darkcube.system.server.item.EquipmentSlot;
import net.minestom.server.item.attribute.ItemAttribute;

public record MinestomAttributeModifierImpl(@NotNull ItemAttribute minestomType) implements MinestomAttributeModifier {
    @Override
    public @NotNull EquipmentSlot equipmentSlot() {
        return EquipmentSlot.of(minestomType.slot());
    }

    @Override
    public @NotNull UUID uniqueId() {
        return minestomType.uuid();
    }
}
