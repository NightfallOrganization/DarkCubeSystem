/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.component.components.Food;
import net.minecraft.world.food.FoodProperties;

public record FoodMapper() implements Mapper<Food, FoodProperties> {
    @Override
    public FoodProperties apply(Food mapping) {
        return new FoodProperties(mapping.nutrition(), mapping.saturationModifier(), mapping.canAlwaysEat(), mapping.eatSeconds(), Optional.ofNullable(mapping.usingConvertsTo()).map(MapperUtil::convert), mapping.effects().stream().map(e -> new FoodProperties.PossibleEffect(MapperUtil.convert(e.effect()), e.probability())).toList());
    }

    @Override
    public Food load(FoodProperties mapping) {
        return new Food(mapping.nutrition(), mapping.saturation(), mapping.canAlwaysEat(), mapping.eatSeconds(), mapping.usingConvertsTo().map(MapperUtil::convert).orElse(null), mapping.effects().stream().map(e -> new Food.EffectChance(MapperUtil.convert(e.effect()), e.probability())).toList());
    }
}
