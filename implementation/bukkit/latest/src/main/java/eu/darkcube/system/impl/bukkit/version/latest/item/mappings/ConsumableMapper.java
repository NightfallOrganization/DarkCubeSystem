/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.component.components.Consumable;
import eu.darkcube.system.server.item.component.components.util.ItemAnimation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemUseAnimation;

public record ConsumableMapper() implements Mapper<Consumable, net.minecraft.world.item.component.Consumable> {
    @Override
    public net.minecraft.world.item.component.Consumable apply(Consumable mapping) {
        var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(mapping.sound().asString())).orElse(SoundEvents.GENERIC_EAT);
        var animation = switch (mapping.animation()) {
            case NONE -> ItemUseAnimation.NONE;
            case EAT -> ItemUseAnimation.EAT;
            case DRINK -> ItemUseAnimation.DRINK;
            case BLOCK -> ItemUseAnimation.BLOCK;
            case BOW -> ItemUseAnimation.BOW;
            case SPEAR -> ItemUseAnimation.SPEAR;
            case CROSSBOW -> ItemUseAnimation.CROSSBOW;
            case SPYGLASS -> ItemUseAnimation.SPYGLASS;
            case TOOT_HORN -> ItemUseAnimation.TOOT_HORN;
            case BRUSH -> ItemUseAnimation.BRUSH;
        };
        var onConsumeEffects = mapping.onConsumeEffects().stream().map(MapperUtil::convert).toList();
        return new net.minecraft.world.item.component.Consumable(mapping.consumeSeconds(), animation, sound, mapping.hasConsumeParticles(), onConsumeEffects);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Consumable load(net.minecraft.world.item.component.Consumable mapping) {
        var consumeSeconds = mapping.consumeSeconds();
        var animation = switch (mapping.animation()) {
            case NONE -> ItemAnimation.NONE;
            case EAT -> ItemAnimation.EAT;
            case DRINK -> ItemAnimation.DRINK;
            case BLOCK -> ItemAnimation.BLOCK;
            case BOW -> ItemAnimation.BOW;
            case SPEAR -> ItemAnimation.SPEAR;
            case CROSSBOW -> ItemAnimation.CROSSBOW;
            case SPYGLASS -> ItemAnimation.SPYGLASS;
            case TOOT_HORN -> ItemAnimation.TOOT_HORN;
            case BRUSH -> ItemAnimation.BRUSH;
        };
        var sound = Key.key(mapping.sound().value().location().toString());
        var hasConsumeParticles = mapping.hasConsumeParticles();
        var onConsumeEffects = mapping.onConsumeEffects().stream().map(MapperUtil::convert).toList();
        return new Consumable(consumeSeconds, animation, sound, hasConsumeParticles, onConsumeEffects);
    }
}
