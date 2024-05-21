/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.util;

import eu.darkcube.system.bukkit.util.BukkitAdventureSupport;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class BukkitAdventureSupportImpl implements BukkitAdventureSupport {
    private final BukkitAudiences audienceProvider;

    public BukkitAdventureSupportImpl(DarkCubeSystemBukkit system) {
        this.audienceProvider = BukkitAudiences.create(system);
    }

    @Override
    public BukkitAudiences audienceProvider() {
        return audienceProvider;
    }
}
