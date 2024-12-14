/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.bukkit.item.BukkitEquipmentSlot;
import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.Equippable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;

public record EquippableMapper() implements Mapper<Equippable, net.minecraft.world.item.equipment.Equippable> {
    @Override
    public net.minecraft.world.item.equipment.Equippable apply(Equippable mapping) {
        var slot = switch (((BukkitEquipmentSlot) mapping.slot()).bukkitType()) {
            case HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case FEET -> EquipmentSlot.FEET;
            case LEGS -> EquipmentSlot.LEGS;
            case CHEST -> EquipmentSlot.CHEST;
            case HEAD -> EquipmentSlot.HEAD;
            case BODY -> EquipmentSlot.BODY;
        };
        var equipSound = BuiltInRegistries.SOUND_EVENT.get(MapperUtil.convert(mapping.equipSound())).orElseThrow();
        var model = Optional.ofNullable(mapping.model()).map(MapperUtil::convert);
        var cameraOverlay = Optional.ofNullable(mapping.cameraOverlay()).map(MapperUtil::convert);
        var allowedEntities = Optional.ofNullable(mapping.allowedEntities()).map(set -> MapperUtil.convert(set, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE));
        return new net.minecraft.world.item.equipment.Equippable(slot, equipSound, model, cameraOverlay, allowedEntities, mapping.dispensable(), mapping.swappable(), mapping.damageOnHurt());
    }

    @Override
    public Equippable load(net.minecraft.world.item.equipment.Equippable mapping) {
        var slot = eu.darkcube.system.server.item.EquipmentSlot.of(switch (mapping.slot()) {
            case MAINHAND -> org.bukkit.inventory.EquipmentSlot.HAND;
            case OFFHAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND;
            case FEET -> org.bukkit.inventory.EquipmentSlot.FEET;
            case LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS;
            case CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST;
            case HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD;
            case BODY -> org.bukkit.inventory.EquipmentSlot.BODY;
        });
        var equipSound = MapperUtil.convertToKey(mapping.equipSound());
        var model = mapping.model().map(MapperUtil::convert).orElse(null);
        var cameraOverlay = mapping.cameraOverlay().map(MapperUtil::convert).orElse(null);
        var allowedEntities = mapping.allowedEntities().map(MapperUtil::convert).orElse(null);
        return new Equippable(slot, equipSound, model, cameraOverlay, allowedEntities, mapping.dispensable(), mapping.swappable(), mapping.damageOnHurt());
    }
}
