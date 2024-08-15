/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.ArrayList;
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

    public static FireworkExplosion.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Shape shape = Shape.SMALL_BALL;
        private final List<RGBLike> colors = new ArrayList<>();
        private final List<RGBLike> fadeColors = new ArrayList<>();
        private boolean trail;
        private boolean twinkle;

        public Builder shape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder withColor(RGBLike color) {
            this.colors.add(color);
            return this;
        }

        public Builder withColor(List<? extends RGBLike> colors) {
            this.colors.addAll(colors);
            return this;
        }

        public Builder withFadeColor(RGBLike color) {
            this.fadeColors.add(color);
            return this;
        }

        public Builder withFadeColor(List<? extends RGBLike> fadeColors) {
            this.fadeColors.addAll(fadeColors);
            return this;
        }

        public Builder withTrail() {
            this.trail = true;
            return this;
        }

        public Builder withTwinkle() {
            this.twinkle = true;
            return this;
        }

        public Builder trail(boolean trail) {
            this.trail = trail;
            return this;
        }

        public Builder twinkle(boolean twinkle) {
            this.twinkle = twinkle;
            return this;
        }

        public FireworkExplosion build() {
            return new FireworkExplosion(shape, colors, fadeColors, trail, twinkle);
        }
    }
}
