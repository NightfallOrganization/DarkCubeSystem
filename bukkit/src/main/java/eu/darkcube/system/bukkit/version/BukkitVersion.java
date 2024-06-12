/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.version;

import java.util.List;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.deprecated.Command;
import eu.darkcube.system.server.version.ServerVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

@Api
public interface BukkitVersion extends ServerVersion {

    static BukkitVersion version() {
        return (BukkitVersion) ServerVersion.version();
    }

    @Api
    BukkitCommandAPIUtils commandApiUtils();

    @Api
    interface BukkitCommandAPIUtils {
        @Api
        String unknownCommandMessage();

        @Api
        List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

        @Api
        void register(Commands.CommandEntry entry);

        @Api
        void unregister(Commands.CommandEntry command);

        @Api
        void unregister(String name);

        @Api
        PluginCommand registerLegacy(Plugin plugin, Command command);

        @Api
        double[] getEntityBB(Entity entity);
    }
}
