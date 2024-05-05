/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.attribute;

import java.util.UUID;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.EquipmentSlot;

public interface AttributeModifier {
    static @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        return AttributeModifierImpl.of(platformAttributeModifier);
    }

    @Nullable EquipmentSlot equipmentSlot();

    @NotNull UUID uniqueId();
}
