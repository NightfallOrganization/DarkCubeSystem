/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.link;

import eu.darkcube.system.link.Link;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public abstract class PluginLink extends Link {
    private final Plugin source;
    private final String pluginName;
    private final PluginLinkListener listener = new PluginLinkListener();

    public PluginLink(Plugin source, String pluginName) throws Throwable {
        super();
        this.source = source;
        this.pluginName = pluginName;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        registerListener();
    }

    @Override
    protected void onDisable() {
        unregisterListener();
        super.onDisable();
    }

    void registerListener() {
        Bukkit.getPluginManager().registerEvents(listener, source);
    }

    void unregisterListener() {
        HandlerList.unregisterAll(listener);
    }

    private class PluginLinkListener implements Listener {
        @EventHandler
        public void handle(PluginDisableEvent event) {
            if (event.getPlugin().getName().equals(pluginName)) {
                disable();
            }
        }
    }
}
