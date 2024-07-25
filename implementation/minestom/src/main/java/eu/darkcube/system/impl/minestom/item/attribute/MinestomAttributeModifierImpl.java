/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.attribute;

import static eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport.adventureSupport;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifier;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import net.minestom.server.item.component.AttributeList;

public record MinestomAttributeModifierImpl(@NotNull AttributeList.Modifier minestomType) implements MinestomAttributeModifier {
    @Override
    public @NotNull EquipmentSlotGroup equipmentSlotGroup() {
        return EquipmentSlotGroup.of(minestomType.slot());
    }

    @Override
    public @NotNull Key key() {
        return adventureSupport().convert(minestomType.modifier().id());
    }

    @Override
    public double amount() {
        return minestomType.modifier().amount();
    }

    @Override
    public @NotNull AttributeModifierOperation operation() {
        return AttributeModifierOperation.of(minestomType.modifier().operation());
    }

    @Override
    public @NotNull Attribute attribute() {
        return Attribute.of(minestomType.attribute());
    }
}
