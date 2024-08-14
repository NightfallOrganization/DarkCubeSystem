/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.DirectMapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.server.item.component.components.DyedItemColor;
import net.minecraft.util.FastColor;

public record DyedColorMapper() implements DirectMapper<DyedItemColor, net.minecraft.world.item.component.DyedItemColor> {
    @Override
    public net.minecraft.world.item.component.DyedItemColor apply(DyedItemColor mapping) {
        return new net.minecraft.world.item.component.DyedItemColor(FastColor.ARGB32.color(0, mapping.color().red(), mapping.color().green(), mapping.color().blue()), mapping.showInTooltip());
    }

    @Override
    public DyedItemColor load(net.minecraft.world.item.component.DyedItemColor mapping) {
        return new DyedItemColor(TextColor.color(FastColor.ARGB32.red(mapping.rgb()), FastColor.ARGB32.green(mapping.rgb()), FastColor.ARGB32.blue(mapping.rgb())), mapping.showInTooltip());
    }
}
