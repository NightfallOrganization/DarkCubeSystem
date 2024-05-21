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
import eu.darkcube.system.impl.bukkit.util.BukkitAdventureSupportImpl;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionHandler;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionImpl;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionLoader;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.link.LinkManager;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

@ApiStatus.Internal
public class DarkCubeSystemBukkit extends DarkCubePlugin implements Listener {
    protected final LinkManager linkManager = new LinkManager();
    private final BukkitVersionHandler versionHandler;

    public DarkCubeSystemBukkit() {
        super("system");
        versionHandler = new BukkitVersionLoader().load();
        DarkCubePlugin.systemPlugin(this);
    }

    @Override
    public void onLoad() {
        versionHandler.onLoad();
        EntityOptions.registerOptions();
        CommandAPI.init();
    }

    @Override
    public void onDisable() {
        AdventureSupport.adventureSupport().audienceProvider().close();
        linkManager.unregisterLinks();
        versionHandler.onDisable();
    }

    @Override
    public void onEnable() {
        InternalProvider.instance().register(AdventureSupport.class, new BukkitAdventureSupportImpl(this));
        versionHandler.onEnable();
        Bukkit.getPluginManager().registerEvents(this, this);
        linkManager.enableLinks();
        BukkitVersionImpl.version().enabled(this);

    }

    @EventHandler
    public void handle(PlayerKickEvent event) {
        if (Objects.equals(event.getReason(), "disconnect.spam")) {
            event.setCancelled(true);
        }
    }
}
