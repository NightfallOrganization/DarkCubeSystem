/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version;

import eu.darkcube.system.bukkit.version.BukkitVersion;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.provider.Provider;

public abstract class BukkitVersionImpl implements BukkitVersion {

    protected BukkitCommandAPIUtils commandApiUtils;
    protected Provider provider;
    protected int protocolVersion = -1;

    public BukkitVersionImpl() {
        provider = Provider.newProvider();
    }

    public static BukkitVersionImpl version() {
        return (BukkitVersionImpl) BukkitVersion.version();
    }

    public void enabled(DarkCubeSystemBukkit system) {
    }

    public void loaded(DarkCubeSystemBukkit system) {
    }

    @Override
    public int protocolVersion() {
        return protocolVersion;
    }

    @Override
    public BukkitCommandAPIUtils commandApiUtils() {
        return commandApiUtils;
    }

    @Override
    public Provider provider() {
        return provider;
    }
}
