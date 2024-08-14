/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import static net.minecraft.util.FastColor.ABGR32.blue;
import static net.minecraft.util.FastColor.ABGR32.green;
import static net.minecraft.util.FastColor.ARGB32.red;

import java.util.ArrayList;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.server.item.component.components.FireworkExplosion;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.FastColor;

public record FireworkExplosionMapper() implements DirectMapper<FireworkExplosion, net.minecraft.world.item.component.FireworkExplosion> {
    private FireworkExplosion.Shape convert(net.minecraft.world.item.component.FireworkExplosion.Shape type) {
        return switch (type) {
            case SMALL_BALL -> FireworkExplosion.Shape.SMALL_BALL;
            case LARGE_BALL -> FireworkExplosion.Shape.LARGE_BALL;
            case STAR -> FireworkExplosion.Shape.STAR;
            case BURST -> FireworkExplosion.Shape.BURST;
            case CREEPER -> FireworkExplosion.Shape.CREEPER;
        };
    }

    private net.minecraft.world.item.component.FireworkExplosion.Shape convert(FireworkExplosion.Shape shape) {
        return switch (shape) {
            case SMALL_BALL -> net.minecraft.world.item.component.FireworkExplosion.Shape.SMALL_BALL;
            case LARGE_BALL -> net.minecraft.world.item.component.FireworkExplosion.Shape.LARGE_BALL;
            case STAR -> net.minecraft.world.item.component.FireworkExplosion.Shape.STAR;
            case CREEPER -> net.minecraft.world.item.component.FireworkExplosion.Shape.CREEPER;
            case BURST -> net.minecraft.world.item.component.FireworkExplosion.Shape.BURST;
        };
    }

    @Override
    public net.minecraft.world.item.component.FireworkExplosion apply(FireworkExplosion mapping) {
        var shape = convert(mapping.shape());
        var colors = new IntArrayList();
        var fadeColors = new IntArrayList();
        for (var color : mapping.colors()) {
            colors.add(FastColor.ARGB32.color(0, color.red(), color.green(), color.blue()));
        }
        for (var color : mapping.fadeColors()) {
            fadeColors.add(FastColor.ARGB32.color(0, color.red(), color.green(), color.blue()));
        }
        return new net.minecraft.world.item.component.FireworkExplosion(shape, colors, fadeColors, mapping.hasTrail(), mapping.hasTwinkle());
    }

    @Override
    public FireworkExplosion load(net.minecraft.world.item.component.FireworkExplosion mapping) {
        var shape = convert(mapping.shape());
        var colors = new ArrayList<RGBLike>();
        var fadeColors = new ArrayList<RGBLike>();
        for (var color : mapping.colors()) {
            colors.add(TextColor.color(red(color), green(color), blue(color)));
        }
        for (var color : mapping.fadeColors()) {
            fadeColors.add(TextColor.color(red(color), green(color), blue(color)));
        }
        return new FireworkExplosion(shape, colors, fadeColors, mapping.hasTrail(), mapping.hasTwinkle());
    }
}
