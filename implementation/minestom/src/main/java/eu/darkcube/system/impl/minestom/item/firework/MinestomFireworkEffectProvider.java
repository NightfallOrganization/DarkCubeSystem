/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.firework;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.firework.FireworkEffect;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;

public class MinestomFireworkEffectProvider implements FireworkEffectProvider {
    @Override
    public @NotNull FireworkEffect of(@NotNull Object platformFireworkEffect) {
        if (platformFireworkEffect instanceof FireworkEffect fireworkEffect) return fireworkEffect;
        if (platformFireworkEffect instanceof net.minestom.server.item.firework.FireworkEffect fireworkEffect) return new MinestomFireworkEffectImpl(fireworkEffect);
        throw new IllegalArgumentException("Invalid FireworkEffect: " + platformFireworkEffect);
    }
}
