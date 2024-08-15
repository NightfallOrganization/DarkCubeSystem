/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.util.List;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.FireworkExplosion;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class FireworkExplosionMapper implements Mapper<FireworkExplosion> {
    @Override
    public void apply(FireworkExplosion mapping, ItemStack item, ItemMeta meta) {
        var effect = FireworkEffect.builder().flicker(mapping.hasTwinkle()).trail(mapping.hasTrail());
        effect.with(convert(mapping.shape()));
        effect.withColor(convert1(mapping.colors()));
        effect.withFade(convert1(mapping.fadeColors()));
        ((FireworkEffectMeta) meta).setEffect(effect.build());
    }

    @Override
    public @Nullable FireworkExplosion convert(ItemStack item, ItemMeta meta) {
        if (!(meta instanceof FireworkEffectMeta fireworkEffectMeta)) return null;
        if (!fireworkEffectMeta.hasEffect()) return null;
        var effect = fireworkEffectMeta.getEffect();
        return new FireworkExplosion(convert(effect.getType()), convert2(effect.getColors()), convert2(effect.getFadeColors()), effect.hasTrail(), effect.hasFlicker());
    }

    private List<RGBLike> convert2(List<Color> l) {
        return l.stream().map(c -> (RGBLike) TextColor.color(c.getRed(), c.getGreen(), c.getBlue())).toList();
    }

    private List<Color> convert1(List<RGBLike> l) {
        return l.stream().map(c -> Color.fromRGB(c.red(), c.green(), c.blue())).toList();
    }

    private FireworkExplosion.Shape convert(FireworkEffect.Type type) {
        return switch (type) {
            case BALL -> FireworkExplosion.Shape.SMALL_BALL;
            case BALL_LARGE -> FireworkExplosion.Shape.LARGE_BALL;
            case STAR -> FireworkExplosion.Shape.STAR;
            case BURST -> FireworkExplosion.Shape.BURST;
            case CREEPER -> FireworkExplosion.Shape.CREEPER;
        };
    }

    private FireworkEffect.Type convert(FireworkExplosion.Shape shape) {
        return switch (shape) {
            case SMALL_BALL -> FireworkEffect.Type.BALL;
            case LARGE_BALL -> FireworkEffect.Type.BALL_LARGE;
            case STAR -> FireworkEffect.Type.STAR;
            case CREEPER -> FireworkEffect.Type.CREEPER;
            case BURST -> FireworkEffect.Type.BURST;
        };
    }
}
