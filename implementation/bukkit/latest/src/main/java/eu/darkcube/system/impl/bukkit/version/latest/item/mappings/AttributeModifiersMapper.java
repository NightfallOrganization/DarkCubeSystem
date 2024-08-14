/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.ArrayList;
import java.util.Objects;

import eu.darkcube.system.impl.bukkit.item.BukkitEquipmentSlotGroupImpl;
import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttribute;
import eu.darkcube.system.impl.bukkit.version.latest.item.attribute.BukkitAttributeModifierOperationImpl;
import eu.darkcube.system.server.item.attribute.Attribute;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import eu.darkcube.system.server.item.component.components.AttributeList;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.bukkit.Registry;

@SuppressWarnings("UnstableApiUsage")
public record AttributeModifiersMapper() implements DirectMapper<AttributeList, ItemAttributeModifiers> {
    @Override
    public ItemAttributeModifiers apply(AttributeList mapping) {
        var list = new ArrayList<ItemAttributeModifiers.Entry>();
        for (var modifier : mapping.modifiers()) {
            var attributeId = ResourceLocation.parse(((BukkitAttribute) modifier.attribute()).bukkitType().key().asString());
            var id = ResourceLocation.parse(modifier.key().asString());
            var attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId).orElseThrow();
            var attributeModifier = new AttributeModifier(id, modifier.amount(), switch (((BukkitAttributeModifierOperationImpl) modifier.operation()).bukkitType()) {
                case ADD_NUMBER -> AttributeModifier.Operation.ADD_VALUE;
                case ADD_SCALAR -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                case MULTIPLY_SCALAR_1 -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            });
            var equipmentSlotGroupString = ((BukkitEquipmentSlotGroupImpl) modifier.equipmentSlotGroup()).bukkitType().toString();
            var equipmentSlotGroup = EquipmentSlotGroup.CODEC.decode(NbtOps.INSTANCE, StringTag.valueOf(equipmentSlotGroupString)).getOrThrow().getFirst();
            list.add(new ItemAttributeModifiers.Entry(attribute, attributeModifier, equipmentSlotGroup));
        }
        return new ItemAttributeModifiers(list, mapping.showInTooltip());
    }

    @Override
    public AttributeList load(ItemAttributeModifiers mapping) {
        var modifiers = new ArrayList<eu.darkcube.system.server.item.attribute.AttributeModifier>();
        for (var modifier : mapping.modifiers()) {
            var attribute = Attribute.of(Objects.requireNonNull(Registry.ATTRIBUTE.get(Key.key(modifier.attribute().unwrapKey().orElseThrow().location().toString()))));
            var key = eu.darkcube.system.libs.net.kyori.adventure.key.Key.key(modifier.modifier().id().toString());
            var equipmentSlotGroup = eu.darkcube.system.server.item.EquipmentSlotGroup.of(Objects.requireNonNull(org.bukkit.inventory.EquipmentSlotGroup.getByName(modifier.slot().getSerializedName())));
            var amount = modifier.modifier().amount();
            var operation = AttributeModifierOperation.of(switch (modifier.modifier().operation()) {
                case ADD_VALUE -> org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
                case ADD_MULTIPLIED_BASE -> org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR;
                case ADD_MULTIPLIED_TOTAL -> org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            });

            modifiers.add(eu.darkcube.system.server.item.attribute.AttributeModifier.of(attribute, key, equipmentSlotGroup, amount, operation));
        }
        return new AttributeList(modifiers, mapping.showInTooltip());
    }
}
