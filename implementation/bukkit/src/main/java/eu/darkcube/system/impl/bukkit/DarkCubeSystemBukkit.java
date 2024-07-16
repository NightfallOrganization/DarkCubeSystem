/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit;

import java.util.Objects;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.argument.EntityOptions;
import eu.darkcube.system.impl.bukkit.commandapi.DarkCubeSystemCommand;
import eu.darkcube.system.impl.bukkit.util.BukkitAdventureSupportImpl;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionHandler;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionLoader;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.link.LinkManager;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class DarkCubeSystemBukkit extends DarkCubePlugin implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkCubeSystem");
    protected final LinkManager linkManager = new LinkManager();
    private final BukkitVersionHandler versionHandler;

    public DarkCubeSystemBukkit() {
        super("system");
        versionHandler = BukkitVersionLoader.INSTANCE.load();
        DarkCubePlugin.systemPlugin(this);
    }

    @Override
    public void onLoad() {
        versionHandler.onLoad(this);
        EntityOptions.registerOptions();
        CommandAPI.init();
    }

    @Override
    public void onDisable() {
        AdventureSupport.adventureSupport().audienceProvider().close();
        linkManager.unregisterLinks();
        versionHandler.onDisable(this);
    }

    @Override
    public void onEnable() {
        InternalProvider.instance().register(AdventureSupport.class, new BukkitAdventureSupportImpl(this));
        versionHandler.onEnable(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        linkManager.enableLinks();
        CommandAPI.instance().register(new DarkCubeSystemCommand());
    }

    @EventHandler
    public void handle(PlayerKickEvent event) {
        if (Objects.equals(event.getReason(), "disconnect.spam")) {
            event.setCancelled(true);
        }
    }

    public Logger getSystemLogger() {
        return LOGGER;
    }
}
