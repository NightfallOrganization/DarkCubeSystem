/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.kyori.wrapper;

import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.platform.AudienceProvider;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class KyoriWrappedAudienceProvider implements AudienceProvider {
    @Override
    public @NotNull Audience all() {
        return null;
    }

    @Override
    public @NotNull Audience console() {
        return null;
    }

    @Override
    public @NotNull Audience players() {
        return null;
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return null;
    }

    @Override
    public @NotNull Audience permission(@NotNull Key permission) {
        return AudienceProvider.super.permission(permission);
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return null;
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        return null;
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return null;
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return null;
    }

    @Override
    public void close() {

    }
}
