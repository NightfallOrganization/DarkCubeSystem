/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item.firework;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.firework.FireworkEffect;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;

public class BukkitFireworkEffectProvider implements FireworkEffectProvider {
    @Override public @NotNull FireworkEffect of(@NotNull Object platformFireworkEffect) {
        if (platformFireworkEffect instanceof FireworkEffect fireworkEffect) return fireworkEffect;
        if (platformFireworkEffect instanceof org.bukkit.FireworkEffect fireworkEffect) return new BukkitFireworkEffectImpl(fireworkEffect);
        throw new IllegalArgumentException("Invalid FireworkEffect: " + platformFireworkEffect);
    }
}
