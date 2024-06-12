/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version;

import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;

public interface BukkitVersionHandler {
    default void onLoad(DarkCubeSystemBukkit system) {
    }

    default void onEnable(DarkCubeSystemBukkit system) {
    }

    default void onDisable(DarkCubeSystemBukkit system) {
    }
}
