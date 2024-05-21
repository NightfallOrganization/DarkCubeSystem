/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

class MinestomInventoryUtils {
    @Nullable
    static Player player(@Nullable Object player) {
        while (true) {
            switch (player) {
                case null -> {
                    return null;
                }
                case Player minestomPlayer -> {
                    return minestomPlayer;
                }
                case User user -> player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(user.uniqueId());
                default -> throw new IllegalArgumentException("Not a valid player type");
            }
        }
    }
}
