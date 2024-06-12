/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.provider.via;

import java.util.UUID;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;

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
    public ProtocolVersion version(UUID uuid) {
        return Via.getAPI().getPlayerProtocolVersion(uuid);
    }

    @Override
    public ProtocolVersion[] supportedVersions() {
        return Via.getManager().getProtocolManager().getSupportedVersions().toArray(ProtocolVersion[]::new);
    }

    @Override
    public ProtocolVersion serverVersion() {
        return Via.getAPI().getServerVersion().highestSupportedProtocolVersion();
    }
}
