/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.adventure;

import java.util.UUID;

import eu.darkcube.system.impl.kyori.wrapper.KyoriWrappedAudience;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.command.PermissionProvider;
import eu.darkcube.system.minestom.util.adventure.MinestomAudienceProvider;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

public class MinestomAudienceProviderImpl implements MinestomAudienceProvider {
    private static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder().build();
    private final KyoriAdventureSupport support;
    private final Audience all = audience(Audiences.all());
    private final Audience console = audience(Audiences.console());
    private final Audience players = audience(Audiences.players());

    public MinestomAudienceProviderImpl(KyoriAdventureSupport support) {
        this.support = support;
    }

    @Override
    public @NotNull Audience all() {
        return all;
    }

    @Override
    public @NotNull Audience console() {
        return console;
    }

    @Override
    public @NotNull Audience players() {
        return players;
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return audience(Audiences.players(p -> p.getUuid().equals(playerId)));
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return audience(Audiences.all(audience -> {
            if (audience instanceof Player player) {
                if (player.getPermissionLevel() >= 2) return true;
            }
            if (audience instanceof CommandSender sender) {
                return PermissionProvider.provider().hasPermission(sender, permission);
            }
            return false;
        }));
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @NotNull Audience world(@NotNull Key world) {
        return audience(Audiences.players(player -> {
            var instance = player.getInstance();
            if (instance == null) return false;
            var instanceKey = Key.key(instance.getDimensionName());
            return instanceKey.equals(world);
        }));
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return all();
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return FLATTENER;
    }

    @Override
    public void close() {
    }

    @Override
    public @NotNull Audience audience(net.kyori.adventure.audience.@NotNull Audience audience) {
        return new KyoriWrappedAudience(audience, support);
    }
}
