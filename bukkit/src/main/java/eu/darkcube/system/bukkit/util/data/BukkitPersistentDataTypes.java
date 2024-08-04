/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.util.data;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitPersistentDataTypes extends PersistentDataTypes {
    public static final PersistentDataType<Location> LOCATION = new PersistentDataType<>() {
        @Override
        public @NotNull Location deserialize(@NotNull JsonElement json) {
            var d = json.getAsJsonObject();
            var x = d.get("x").getAsDouble();
            var y = d.get("y").getAsDouble();
            var z = d.get("z").getAsDouble();
            var yaw = d.get("yaw").getAsFloat();
            var pitch = d.get("pitch").getAsFloat();
            var world = Bukkit.getWorld(java.util.UUID.fromString(d.get("world").getAsString()));
            return new Location(world, x, y, z, yaw, pitch);
        }

        @Override
        public @NotNull JsonElement serialize(@NotNull Location data) {
            var d = new JsonObject();
            d.addProperty("x", data.getX());
            d.addProperty("y", data.getY());
            d.addProperty("z", data.getZ());
            d.addProperty("yaw", data.getYaw());
            d.addProperty("pitch", data.getPitch());
            d.addProperty("world", data.getWorld().getUID().toString());
            return d;
        }

        @Override
        public @NotNull Location clone(@NotNull Location object) {
            return object.clone();
        }
    };
}
