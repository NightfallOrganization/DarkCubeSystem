/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;

public record Color(int rgb) implements RGBLike {
    private static final ColorProvider PROVIDER = InternalProvider.instance().instance(ColorProvider.class);

    public Color(int rgb, boolean hasAlpha) {
        this(hasAlpha ? rgb : 0xFF000000 | rgb);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF)));
    }

    public static @NotNull Color color(@NotNull Object color) {
        return PROVIDER.color(color);
    }

    /**
     * Returns the red component in the range 0-255 in the default sRGB
     * space.
     *
     * @return the red component.
     * @see #rgb()
     */
    @Override
    public int red() {
        return (rgb() >> 16) & 0xFF;
    }

    /**
     * Returns the green component in the range 0-255 in the default sRGB
     * space.
     *
     * @return the green component.
     * @see #rgb()
     */
    @Override
    public int green() {
        return (rgb() >> 8) & 0xFF;
    }

    /**
     * Returns the blue component in the range 0-255 in the default sRGB
     * space.
     *
     * @return the blue component.
     * @see #rgb()
     */
    @Override
    public int blue() {
        return (rgb()) & 0xFF;
    }

    /**
     * Returns the alpha component in the range 0-255.
     *
     * @return the alpha component.
     * @see #rgb()
     */
    public int alpha() {
        return (rgb() >> 24) & 0xff;
    }
}
