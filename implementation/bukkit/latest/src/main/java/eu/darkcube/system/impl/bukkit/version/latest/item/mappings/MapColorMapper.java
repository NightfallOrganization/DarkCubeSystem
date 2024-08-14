/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.component.MapItemColor;

public record MapColorMapper() implements DirectMapper<RGBLike, MapItemColor> {
    @Override
    public MapItemColor apply(RGBLike mapping) {
        return new MapItemColor(FastColor.ARGB32.color(0, mapping.red(), mapping.green(), mapping.blue()));
    }

    @Override
    public RGBLike load(MapItemColor mapping) {
        return TextColor.color(FastColor.ARGB32.red(mapping.rgb()), FastColor.ARGB32.green(mapping.rgb()), FastColor.ARGB32.blue(mapping.rgb()));
    }
}
