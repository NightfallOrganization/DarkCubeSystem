/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.commandapi;

import java.util.List;

import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.version.BukkitVersion;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

@ApiStatus.Internal public class InternalCommandTabExecutor implements TabExecutor {
    private static String join(String label, String[] args) {
        return args.length == 0 ? label : (label + " " + String.join(" ", args));
    }

    @Override public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return BukkitVersion.version().commandApiUtils().tabComplete(sender, command, label, args);
    }

    @Override public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        var commandLine = join(label, args);
        CommandAPI.instance().getCommands().executeCommand(sender, commandLine);
        return true;
    }
}
