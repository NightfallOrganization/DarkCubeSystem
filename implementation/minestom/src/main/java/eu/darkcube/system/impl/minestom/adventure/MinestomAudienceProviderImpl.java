/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.adventure;

import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.util.adventure.MinestomAudienceProvider;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.PermissionHandler;

public class MinestomAudienceProviderImpl implements MinestomAudienceProvider {
    private static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder().build();

    @Override public @NotNull Audience all() {
        return MinestomAudience.ALL;
    }

    @Override public @NotNull Audience console() {
        return MinestomAudience.CONSOLE;
    }

    @Override public @NotNull Audience players() {
        return MinestomAudience.PLAYERS;
    }

    @Override public @NotNull Audience player(@NotNull UUID playerId) {
        return new MinestomAudience(Audiences.players(p -> p.getUuid().equals(playerId)));
    }

    @Override public @NotNull Audience permission(@NotNull String permission) {
        return new MinestomAudience(Audiences.all(audience -> {
            if (audience instanceof Player player) {
                if (player.getPermissionLevel() >= 2) return true;
            }
            if (audience instanceof PermissionHandler handler) {
                return handler.hasPermission(permission);
            }
            return false;
        }));
    }

    @Override public @NotNull Audience world(@NotNull Key world) {
        return new MinestomAudience(Audiences.players(player -> {
            var instance = player.getInstance();
            if (instance == null) return false;
            var instanceKey = Key.key(instance.getDimensionName());
            return instanceKey.equals(world);
        }));
    }

    @Override public @NotNull Audience server(@NotNull String serverName) {
        return all();
    }

    @Override public @NotNull ComponentFlattener flattener() {
        return FLATTENER;
    }

    @Override public void close() {
    }

    @Override public Audience audience(net.kyori.adventure.audience.Audience audience) {
        return new MinestomAudience(audience);
    }
}
