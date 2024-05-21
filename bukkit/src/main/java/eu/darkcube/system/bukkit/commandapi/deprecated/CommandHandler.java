/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.darkcube.system.bukkit.commandapi.deprecated.SpacedCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class CommandHandler implements TabExecutor {

    private CommandAPI instance;

    public CommandHandler(CommandAPI instance) {
        this.instance = instance;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        var CMD = instance.main_command;
        if (!CMD.hasPermission(sender)) return Collections.emptyList();
        var oldCMD = CMD;
        var cancel = false;
        var level = 0;
        var lastArg = args[level];

        while (!cancel && CMD.hasSubCommands() && level < args.length) {
            lastArg = args[level];
            var c = Command.getCommand(CMD.getChilds(), lastArg);
            if (c == null) {
                cancel = true;
            } else {
                oldCMD = CMD;
                CMD = c;
                if (c instanceof ISpaced && args.length > level + 1) {
                    ((ISpaced) c).setSpaced(args[++level]);
                    lastArg = args[level];
                }
            }
            level++;
        }

        var lastArgs = new String[args.length - level];
        for (var i = 0; level < args.length; level++, i++)
            lastArgs[i] = args[level];

        if (CMD.onTabComplete(lastArgs) == null) {
            List<String> players = new ArrayList<>();
            for (var p : Bukkit.getOnlinePlayers())
                players.add(p.getName());
            return players;
        }

        if (!CMD.onTabComplete(lastArgs).isEmpty()) {
            return CMD.onTabComplete(lastArgs);
        }

        if (!(CMD instanceof ISpaced) && !(CMD instanceof SubCommand)) {
            if ((level - 1) != CMD.getPosition().getPosition()) {
                CMD = oldCMD;
            }
        }

        if (cancel) {
            if (CMD.onTabComplete(lastArgs).isEmpty()) {
                return Command.vv(sender, CMD.getChilds(), lastArg);
            }
            return CMD.onTabComplete(lastArgs);
        }

        if (lastArgs.length != 0) return CMD.onTabComplete(lastArgs);

        if (CMD instanceof ISpaced) {
            return CMD.onTabComplete(new String[]{lastArg});
        }

        return Command.vv(sender, CMD.getChilds(), lastArg);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

        var CMD = instance.main_command;

        if (args.length == 0 && CMD.hasSubCommands()) {
            CMD.sendUsage(sender);
            return true;
        } else if (args.length == 0) {
            CMD.onCommand(sender, args, "");
            return true;
        }

        var cancel = false;
        var level = 0;
        var lastArg = args[level];

        while (!cancel && CMD.hasSubCommands() && level < args.length) {
            lastArg = args[level];
            var c = Command.getCommand(CMD.getChilds(), lastArg);
            if (c == null) {
                cancel = true;
            } else {
                CMD = c;
                if (c instanceof ISpaced && args.length > level + 1) {
                    ((ISpaced) c).setSpaced(args[++level]);
                }
            }
            level++;
        }

        if (cancel) {
            CMD.sendUsage(sender);
            return true;
        }

        var lastArgs = new String[args.length - level];
        for (var i = 0; level < args.length; level++, i++)
            lastArgs[i] = args[level];

        if (lastArg.equalsIgnoreCase(CMD.getName())) lastArg = "";

        CMD.onCommand(sender, lastArgs, lastArg);
        return true;
    }
}
