/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.bukkit.version.BukkitVersion;

public class CommandAPI {

    private static CommandAPI instance;
    private Commands commands;

    private CommandAPI() {
        if (CommandAPI.instance != null) {
            throw new IllegalAccessError("You may not initialize the CommandAPI twice!");
        }
        this.commands = new Commands();
        CommandAPI.instance = this;
    }

    /**
     * Deprecated in favor of {@link #instance()}.
     *
     * @see #instance()
     */
    @Deprecated(forRemoval = true)
    public static CommandAPI getInstance() {
        return instance();
    }

    @Api
    public static CommandAPI instance() {
        return CommandAPI.instance;
    }

    public static CommandAPI init() {
        return new CommandAPI();
    }

    public void register(Command command) {
        var entry = commands.register(command);
        BukkitVersion.version().commandApiUtils().register(entry);
    }

    public void unregister(Command command) {
        var entry = commands.unregister(command);
        if (entry != null) {
            BukkitVersion.version().commandApiUtils().unregister(entry);
        }
    }

    public void unregisterByPrefix(String prefix) {
        commands.unregisterByPrefix(prefix);
    }

    public void unregisterPrefixlessByPrefix(String prefix) {
        commands.unregisterPrefixlessByPrefix(prefix);
    }

    public void unregisterAll() {
        this.commands = new Commands();
    }

    public Commands getCommands() {
        return this.commands;
    }

}
