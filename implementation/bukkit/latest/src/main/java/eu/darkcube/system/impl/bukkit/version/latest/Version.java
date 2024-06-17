/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import java.util.ServiceLoader;
import java.util.logging.Level;

import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.bukkit.util.WorkbenchUtil;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionImpl;
import eu.darkcube.system.impl.bukkit.version.latest.provider.via.ViaSupportImpl;
import eu.darkcube.system.impl.bukkit.version.latest.util.WorkbenchUtilImpl;
import net.minecraft.SharedConstants;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Version extends BukkitVersionImpl {
    public Version() {
        this.commandApiUtils = new CommandAPIUtilsImpl();
        this.protocolVersion = SharedConstants.getProtocolVersion();
        provider.register(WorkbenchUtil.class, new WorkbenchUtilImpl());
    }

    @Override
    public void loaded(DarkCubeSystemBukkit system) {
        ServiceLoader.load(LatestCloudNetImplementation.class, getClass().getClassLoader()).findFirst().ifPresent(LatestCloudNetImplementation::init);
        super.loaded(system);
        try {
            provider.register(ViaSupport.class, new ViaSupportImpl());
            system.getLogger().info("Enabled ViaSupport");
        } catch (Throwable e) {
            system.getLogger().log(Level.WARNING, "Failed to enable ViaSupport", e);
            provider.register(ViaSupport.class, ViaSupport.wrapper(null));
        }
    }

    @Override
    public void enabled(DarkCubeSystemBukkit system) {
        super.enabled(system);
        Bukkit.getPluginManager().registerEvents((Listener) commandApiUtils, system);
        ((CommandAPIUtilsImpl) commandApiUtils).enabled(system);
    }
}
