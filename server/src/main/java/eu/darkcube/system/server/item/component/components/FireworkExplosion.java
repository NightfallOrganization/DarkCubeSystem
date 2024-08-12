/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record FireworkExplosion(@NotNull Shape shape, @NotNull List<RGBLike> colors, @NotNull List<RGBLike> fadeColors, boolean hasTrail, boolean hasTwinkle) {
    public FireworkExplosion {
        colors = List.copyOf(colors);
        fadeColors = List.copyOf(fadeColors);
    }

    public enum Shape {
        SMALL_BALL,
        LARGE_BALL,
        STAR,
        CREEPER,
        BURST
    }
}
