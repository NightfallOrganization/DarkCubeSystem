/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitCommandExecutorConfigureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private BukkitCommandExecutorImpl executor;

    public BukkitCommandExecutorConfigureEvent(BukkitCommandExecutorImpl executor) {
        this.executor = executor;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BukkitCommandExecutorImpl getExecutor() {
        return executor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
