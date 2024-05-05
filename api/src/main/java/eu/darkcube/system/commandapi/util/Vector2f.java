/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import eu.darkcube.system.annotations.Api;

@Api public class Vector2f {
    @Api
    public static final Vector2f ZERO = new Vector2f(0.0F, 0.0F);
    @Api
    public static final Vector2f ONE = new Vector2f(1.0F, 1.0F);
    @Api
    public static final Vector2f UNIT_X = new Vector2f(1.0F, 0.0F);
    @Api
    public static final Vector2f NEGATIVE_UNIT_X = new Vector2f(-1.0F, 0.0F);
    @Api
    public static final Vector2f UNIT_Y = new Vector2f(0.0F, 1.0F);
    @Api
    public static final Vector2f NEGATIVE_UNIT_Y = new Vector2f(0.0F, -1.0F);
    @Api
    public static final Vector2f MAX = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    @Api
    public static final Vector2f MIN = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
    @Api
    public final float x;
    @Api
    public final float y;

    @Api public Vector2f(float xIn, float yIn) {
        this.x = xIn;
        this.y = yIn;
    }

    public boolean equals(Vector2f other) {
        return this.x == other.x && this.y == other.y;
    }
}
