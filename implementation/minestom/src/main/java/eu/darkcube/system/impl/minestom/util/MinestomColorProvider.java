/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Color;
import eu.darkcube.system.util.ColorProvider;
import net.kyori.adventure.util.RGBLike;

public class MinestomColorProvider implements ColorProvider {
    @Override
    public @NotNull Color color(@NotNull Object color) {
        return switch (color) {
            case Color c -> c;
            case RGBLike c -> new Color(asRGB(c), false);
            case java.awt.Color c -> new Color(c.getRGB());
            case Integer c -> new Color(c);
            default -> throw new IllegalArgumentException("Invalid color: " + color);
        };
    }

    private static int asRGB(RGBLike c) {
        var rgb = c.red();
        rgb = (rgb << 8) + c.green();
        return (rgb << 8) + c.blue();
    }
}
