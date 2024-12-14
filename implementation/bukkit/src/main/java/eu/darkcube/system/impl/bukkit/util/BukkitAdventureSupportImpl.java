/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.util;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.util.BukkitAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class BukkitAdventureSupportImpl implements BukkitAdventureSupport {
    private BukkitAudiences audienceProvider;

    @Override
    public BukkitAudiences audienceProvider() {
        if (audienceProvider != null) return audienceProvider;
        synchronized (this) {
            if (audienceProvider != null) return audienceProvider;
            audienceProvider = BukkitAudiences.create(DarkCubePlugin.systemPlugin());
            return audienceProvider;
        }
    }
}
