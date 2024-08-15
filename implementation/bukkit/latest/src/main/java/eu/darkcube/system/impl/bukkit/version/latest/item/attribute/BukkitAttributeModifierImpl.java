/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;
import static net.minecraft.world.entity.EquipmentSlotGroup.*;

import eu.darkcube.system.bukkit.item.attribute.BukkitAttributeModifier;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.EquipmentSlotGroup;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import org.bukkit.attribute.AttributeModifier;

public record BukkitAttributeModifierImpl(@NotNull AttributeModifier bukkitType, @NotNull Attribute attribute, net.minecraft.world.entity.EquipmentSlotGroup nmsEquipmentSlotGroup) implements BukkitAttributeModifier {
    public BukkitAttributeModifierImpl(@NotNull AttributeModifier bukkitType, @NotNull Attribute attribute) {
        this(bukkitType, attribute, convert(bukkitType.getSlotGroup()));
    }

    @Override
    public @NotNull @Unmodifiable EquipmentSlotGroup equipmentSlotGroup() {
        return EquipmentSlotGroup.of(bukkitType.getSlotGroup());
    }

    @Override
    public @NotNull Key key() {
        return adventureSupport().convert(bukkitType.key());
    }

    @Override
    public double amount() {
        return bukkitType.getAmount();
    }

    @Override
    public @NotNull AttributeModifierOperation operation() {
        return AttributeModifierOperation.of(bukkitType.getOperation());
    }

    private static net.minecraft.world.entity.EquipmentSlotGroup convert(org.bukkit.inventory.EquipmentSlotGroup group) {
        if (group == org.bukkit.inventory.EquipmentSlotGroup.ANY) return ANY;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.ARMOR) return ARMOR;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.BODY) return BODY;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.CHEST) return CHEST;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.FEET) return FEET;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.HAND) return HAND;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.HEAD) return HEAD;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.LEGS) return LEGS;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.MAINHAND) return MAINHAND;
        if (group == org.bukkit.inventory.EquipmentSlotGroup.OFFHAND) return OFFHAND;
        throw new IllegalArgumentException(String.valueOf(group));
    }
}
