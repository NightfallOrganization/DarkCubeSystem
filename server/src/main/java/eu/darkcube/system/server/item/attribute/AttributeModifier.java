/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.attribute;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.EquipmentSlotGroup;

public interface AttributeModifier {
    static @NotNull AttributeModifier of(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        return AttributeModifierImpl.of(attribute, key, equipmentSlotGroup, amount, operation);
    }

    static @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        return AttributeModifierImpl.of(platformAttributeModifier);
    }

    @NotNull
    @Unmodifiable
    EquipmentSlotGroup equipmentSlotGroup();

    @NotNull
    Key key();

    double amount();

    @NotNull
    AttributeModifierOperation operation();

    @NotNull
    Attribute attribute();
}
