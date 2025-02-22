/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.MapItemColor;

public record MapColorMapper() implements Mapper<RGBLike, MapItemColor> {
    @Override
    public MapItemColor apply(RGBLike mapping) {
        return new MapItemColor(ARGB.color(0, mapping.red(), mapping.green(), mapping.blue()));
    }

    @Override
    public RGBLike load(MapItemColor mapping) {
        return TextColor.color(ARGB.red(mapping.rgb()), ARGB.green(mapping.rgb()), ARGB.blue(mapping.rgb()));
    }
}
