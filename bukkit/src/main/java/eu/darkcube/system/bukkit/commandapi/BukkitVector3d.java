/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.commandapi.util.Vector3d;
import org.bukkit.Location;

public final class BukkitVector3d {
    public static Vector3d position(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }
}
