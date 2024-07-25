/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.attribute;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.EquipmentSlotGroup;

class AttributeModifierImpl {
    private static final AttributeModifierProvider provider = InternalProvider.instance().instance(AttributeModifierProvider.class);

    public static @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        return provider.of(platformAttributeModifier);
    }

    public static AttributeModifier of(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        return provider.of(attribute, key, equipmentSlotGroup, amount, operation);
    }
}
