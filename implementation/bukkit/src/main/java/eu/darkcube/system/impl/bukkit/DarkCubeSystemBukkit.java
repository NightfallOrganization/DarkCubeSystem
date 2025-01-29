/*
 * Copyright (c) 2023-2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit;

import java.lang.ref.Reference;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.argument.EntityOptions;
import eu.darkcube.system.impl.bukkit.commandapi.DarkCubeSystemCommand;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionHandler;
import eu.darkcube.system.impl.bukkit.version.BukkitVersionLoader;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.link.LinkManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class DarkCubeSystemBukkit extends DarkCubePlugin implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkCubeSystem");
    protected final LinkManager linkManager = new LinkManager();
    private final BukkitVersionHandler versionHandler;
    private final Set<User> onlineUsers = new HashSet<>();

    public DarkCubeSystemBukkit() {
        super("system");
        DarkCubePlugin.systemPlugin(this);
        versionHandler = BukkitVersionLoader.INSTANCE.load();
    }

    @Override
    public void onLoad() {
        versionHandler.onLoad(this);
        EntityOptions.registerOptions();
        CommandAPI.init();
    }

    @Override
    public void onDisable() {
        linkManager.unregisterLinks();
        versionHandler.onDisable(this);
        AdventureSupport.adventureSupport().audienceProvider().close();
    }

    @Override
    public void onEnable() {
        versionHandler.onEnable(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        linkManager.enableLinks();
        CommandAPI.instance().register(new DarkCubeSystemCommand());
        AdventureSupport.adventureSupport().audienceProvider();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(AsyncPlayerPreLoginEvent event) {
        try {
            var user = UserAPI.instance().user(event.getUniqueId());
            Reference.reachabilityFence(user);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Reference.reachabilityFence(user);
                }
            }.runTaskLater(this, 10 * 60 * 20); // 10 minutes
        } catch (Throwable t) {
            LOGGER.error("Error while ensuring user reachability", t);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerLoginEvent event) {
        try {
            var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
            Reference.reachabilityFence(user);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Reference.reachabilityFence(user);
                }
            }.runTaskLater(this, 10);
        } catch (Throwable t) {
            LOGGER.error("Error while ensuring user reachability", t);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerJoinEvent event) {
        try {
            var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
            onlineUsers.add(user);
            Reference.reachabilityFence(user);
        } catch (Throwable t) {
            LOGGER.error("Error while ensuring user reachability", t);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handle(PlayerQuitEvent event) {
        try {
            var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
            if (!onlineUsers.remove(user)) {
                LOGGER.error("User {} was not in onlineUsers list when quitting", event.getPlayer().getName());
            }
            Reference.reachabilityFence(user);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Reference.reachabilityFence(user);
                }
            }.runTaskLater(this, 10);
        } catch (Throwable t) {
            LOGGER.error("Error while ensuring user reachability", t);
        }
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
