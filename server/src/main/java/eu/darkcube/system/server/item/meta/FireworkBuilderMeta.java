/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.meta;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.firework.FireworkEffect;

@Api
public final class FireworkBuilderMeta implements BuilderMeta {
    private @Nullable FireworkEffect fireworkEffect;

    public FireworkBuilderMeta(@Nullable FireworkEffect fireworkEffect) {
        this.fireworkEffect = fireworkEffect;
    }

    public FireworkBuilderMeta(@Nullable Object fireworkEffect) {
        this(fireworkEffect == null ? null : FireworkEffect.of(fireworkEffect));
    }

    public FireworkBuilderMeta() {
    }

    @Api
    public @Nullable FireworkEffect fireworkEffect() {
        return fireworkEffect;
    }

    @Api
    public @NotNull FireworkBuilderMeta fireworkEffect(@Nullable FireworkEffect fireworkEffect) {
        this.fireworkEffect = fireworkEffect;
        return this;
    }

    @Api
    public @NotNull FireworkBuilderMeta fireworkEffect(@Nullable Object fireworkEffect) {
        if (fireworkEffect == null) return fireworkEffect(null);
        return fireworkEffect(FireworkEffect.of(fireworkEffect));
    }

    @Override
    public FireworkBuilderMeta clone() {
        return new FireworkBuilderMeta(fireworkEffect);
    }
}
