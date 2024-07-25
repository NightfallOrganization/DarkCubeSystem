/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.attribute;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.MinestomEquipmentSlotGroup;
import eu.darkcube.system.minestom.item.attribute.MinestomAttribute;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifierOperation;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifier;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import net.minestom.server.item.component.AttributeList;

public class MinestomAttributeModifierProvider implements AttributeModifierProvider {
    @Override
    public @NotNull AttributeModifier of(@NotNull Object platformAttributeModifier) {
        if (platformAttributeModifier instanceof AttributeModifier attributeModifier) return attributeModifier;
        if (platformAttributeModifier instanceof AttributeList.Modifier modifier) return new MinestomAttributeModifierImpl(modifier);
        throw new IllegalArgumentException("Invalid AttributeModifier: " + platformAttributeModifier);
    }

    @Override
    public @NotNull AttributeModifier of(@NotNull Attribute attribute, @NotNull Key key, @NotNull EquipmentSlotGroup equipmentSlotGroup, double amount, @NotNull AttributeModifierOperation operation) {
        var minestomAttribute = ((MinestomAttribute) attribute).minestomType();
        var modifier = new net.minestom.server.entity.attribute.AttributeModifier(key.toString(), amount, ((MinestomAttributeModifierOperation) operation).minestomType());
        var slot = ((MinestomEquipmentSlotGroup) equipmentSlotGroup).minestomType();
        return of(new AttributeList.Modifier(minestomAttribute, modifier, slot));
    }
}
