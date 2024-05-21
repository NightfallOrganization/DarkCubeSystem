/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.provider.via;

import java.util.UUID;

import com.viaversion.viaversion.api.Via;

public abstract class AbstractViaSupport implements ViaSupport {
    public AbstractViaSupport() {
        Via.getAPI();
        init();
    }

    public abstract void init();

    @Override
    public boolean supported() {
        return true;
    }

    @Override
    public int version(UUID uuid) {
        return Via.getAPI().getPlayerVersion(uuid);
    }

    @Override
    public int[] supportedVersions() {
        return Via.getManager().getProtocolManager().getSupportedVersions().stream().mapToInt(i -> i).toArray();
    }

    @Override
    public int serverVersion() {
        return Via.getAPI().getServerVersion().highestSupportedVersion();
    }
}
