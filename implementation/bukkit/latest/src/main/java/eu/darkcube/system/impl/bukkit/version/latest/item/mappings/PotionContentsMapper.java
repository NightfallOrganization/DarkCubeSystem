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
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.server.item.component.components.PotionContents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.alchemy.Potion;

public record PotionContentsMapper() implements Mapper<PotionContents, net.minecraft.world.item.alchemy.PotionContents> {
    @Override
    public net.minecraft.world.item.alchemy.PotionContents apply(PotionContents mapping) {
        var potion = Optional.ofNullable(mapping.potion()).map(k -> ResourceLocation.parse(k.toString())).<Holder<Potion>>flatMap(BuiltInRegistries.POTION::getHolder);
        var color = Optional.ofNullable(mapping.customColor()).map(c -> FastColor.ARGB32.color(0, c.red(), c.green(), c.blue()));

        return new net.minecraft.world.item.alchemy.PotionContents(potion, color, mapping.customEffects().stream().map(MapperUtil::convert).toList());
    }

    @Override
    public PotionContents load(net.minecraft.world.item.alchemy.PotionContents mapping) {
        var potion = mapping.potion().flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).map(Key::key).orElse(null);
        var customColor = mapping.customColor().map(c -> TextColor.color(FastColor.ARGB32.red(c), FastColor.ARGB32.green(c), FastColor.ARGB32.blue(c))).orElse(null);
        return new PotionContents(potion, customColor, mapping.customEffects().stream().map(MapperUtil::convert).toList());
    }
}
