/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import eu.darkcube.system.impl.bukkit.item.BukkitEquipmentSlotGroupImpl;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import org.bukkit.NamespacedKey;

public class BukkitAttributeModifierProvider implements AttributeModifierProvider {
    @Override
    public @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        if (platformAttributeModifier instanceof AttributeModifier attributeModifier) return attributeModifier;
        // if (platformAttributeModifier instanceof org.bukkit.attribute.AttributeModifier attributeModifier)
        //     return new BukkitAttributeModifierImpl(attributeModifier);
        throw new IllegalArgumentException("Invalid Attribute Modifier: " + platformAttributeModifier);
    }

    @Override
    public @NotNull AttributeModifier of(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        var bukkitOperation = ((BukkitAttributeModifierOperationImpl) operation).bukkitType();
        var bukkitSlot = ((BukkitEquipmentSlotGroupImpl) equipmentSlotGroup).bukkitType();
        var bukkitType = new org.bukkit.attribute.AttributeModifier(new NamespacedKey(key.namespace(), key.value()), amount, bukkitOperation, bukkitSlot);
        return new BukkitAttributeModifierImpl(bukkitType, attribute);
    }
}
