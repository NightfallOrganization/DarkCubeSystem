/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.darkcube.system.bukkit.version.BukkitVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Command implements Taber {

    // static final Map<CommandAPI, Set<Command>> COMMANDS = new HashMap<>();

    CommandAPI instance;
    private String name;
    private String permission;
    private Set<Command> childs;
    private CommandPosition pos;
    private Argument[] arguments;
    private String[] aliases;
    private String simpleUsage;
    private String simpleLongUsage;
    private String beschreibung;

    public Command(JavaPlugin plugin, String name, Command[] childs, String beschreibung, Argument... arguments) {
        this.name = name;
        this.childs = values(childs);
        this.arguments = arguments;
        this.beschreibung = beschreibung;
        try {
            this.permission = "system.plugin." + plugin.getName().toLowerCase().replace(" ", "") + ".command";
        } catch (NullPointerException ex) {
            throw new InvalidCommandException("Please call the Mehthod CommandAPI#install before initializing the Command. " + "Keep in mind that the code where the installing and initializing may have to be synchronized");
        }
        reloadPermissions();
        loadSimpleUsage();
        setAliases();
    }

    public Command(JavaPlugin plugin, String name, Command[] childs, String beschreibung) {
        this(plugin, name, childs, beschreibung, new Argument[0]);
    }

    public static List<String> vv(CommandSender sender, Set<Command> commands, String start) {
        return Command.value(sender, Command.value(commands), start);
    }

    public static Set<Command> values(Command[] commands) {
        List<Command> cmds = new ArrayList<>();
        Collections.addAll(cmds, commands);
        return values(cmds);
    }

    public static Set<Command> values(List<Command> commands) {
        return new HashSet<>(commands);
    }

    public static List<Command> value(Set<Command> commands) {
        return new ArrayList<>(commands);
    }

    public static List<String> value(CommandSender sender, List<Command> commands, String start) {
        List<String> sol = new ArrayList<>();
        for (var cmd : commands) {
            if (cmd.hasPermission(sender)) if (cmd.getName().toLowerCase().startsWith(start.toLowerCase())) sol.add(cmd.getName());
        }
        return sol;
    }

    public static Command getCommand(Set<Command> commands, String cmd) {
        for (var c : commands) {
            for (var alias : c.getAliases()) {
                if (alias.equalsIgnoreCase(cmd)) return c;
            }
            if (c.getName().equalsIgnoreCase(cmd)) return c;
        }
        return null;
    }

    public String getSimpleLongUsage() {
        return simpleLongUsage;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String... aliases) {
        this.aliases = aliases;
    }

    private void loadSimpleUsage() {
        var usage = new StringBuilder();
        usage.append(this.getName());
        for (var argument : this.arguments) {
            if (argument.isNeeded()) {
                usage.append(' ').append('<').append(argument.getName()).append('>');
            } else {
                usage.append(' ').append('[').append(argument.getName()).append(']');
            }
        }
        this.simpleUsage = usage.toString();
    }

    private Command findMainCommand() {
        return instance.getMainCommand();
    }

    final void loadSimpleLongUsage() {
        var cmd = findMainCommand();
        var usageBuilder = new StringBuilder();
        usageBuilder.append('§').append('7').append('/');
        loadSimpleLongUsageRecursiveCall(cmd, usageBuilder, true);
        this.simpleLongUsage = usageBuilder.toString();
        for (var child : this.getChilds()) {
            child.loadSimpleLongUsage();
        }
    }

    private boolean loadSimpleLongUsageRecursiveCall(Command cmd, StringBuilder builder) {
        return loadSimpleLongUsageRecursiveCall(cmd, builder, false);
    }

    private boolean loadSimpleLongUsageRecursiveCall(Command cmd, StringBuilder builder, boolean first) {
        if (!first) {
            builder.append(' ');
        }
        if (cmd.equals(this)) {
            builder.append(cmd.simpleUsage);
            return true;
        }
        for (var c : cmd.getChilds()) {
            var commandBuilder = new StringBuilder();
            if (loadSimpleLongUsageRecursiveCall(c, commandBuilder)) {
                builder.append(cmd.simpleUsage);
                builder.append(commandBuilder);
                return true;
            }
        }
        return false;
    }

    public void reloadPermissions() {
        var p = getName().toLowerCase();
        this.permission += '.' + p;
        for (var child : childs) {
            child.permission = this.permission;
            child.reloadPermissions();
        }
    }

    public final void onCommand(CommandSender sender, String[] args, String start) {
        if (hasPermission(sender)) {
            if (!execute(sender, args)) {
                sendUsage(sender, start);
            }
        } else sender.sendMessage(BukkitVersion.version().commandApiUtils().unknownCommandMessage());
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return Collections.emptyList();
    }

    public final String getUsage(CommandSender sender) {
        return getUsage(sender, "");
    }

    public final String getUsage(CommandSender sender, String start) {
        if (hasPermission(sender)) {
            var builder = new StringBuilder();
            if (hasSubCommands()) {
                builder.append(getUsageCommands(sender, start));
            } else {
                var niceCommandName = this.name;
                if (!niceCommandName.isEmpty()) niceCommandName = Character.toString(niceCommandName.charAt(0)).toUpperCase() + niceCommandName.substring(1);
                builder.append(this.instance.prefix).append("§3§lCommand Beschreibung - §b").append(niceCommandName).append('\n').append(this.instance.prefix).append("§b> §7").append(this.beschreibung);
                if (arguments.length != 0) {
                    builder.append('\n').append(this.instance.prefix).append("§3Argumente: ");
                    for (var argument : arguments) {
                        builder.append('\n').append(this.instance.prefix).append("§b- §8");
                        if (argument.isNeeded()) {
                            builder.append("<§7").append(argument.getName()).append("§8>");
                        } else {
                            builder.append("[§7").append(argument.getName()).append("§8]");
                        }
                        if (argument.getDescription() != null && !argument.getDescription().isEmpty()) {
                            builder.append("§3 -> §7").append(argument.getDescription());
                        }
                    }
                }
            }
            return builder.toString();
        }
        return BukkitVersion.version().commandApiUtils().unknownCommandMessage();
    }

    private String getUsageCommands(CommandSender sender, String start) {
        if (hasPermission(sender)) {
            if (hasSubCommands()) {
                var usage = new StringBuilder();
                var niceCommandName = this.name;
                if (!niceCommandName.isEmpty()) niceCommandName = Character.toString(niceCommandName.charAt(0)).toUpperCase() + niceCommandName.substring(1);
                usage.append(instance.prefix).append("§b").append(niceCommandName).append(" Sub Commands: ");
                usage.append("§7(").append(this.simpleLongUsage).append("§7)");

                for (var cmd : getChilds()) {
                    if (hasPermission(sender, cmd.getPermission())) {
                        if (cmd.getName().toLowerCase().startsWith(start.toLowerCase())) {
                            usage.append("\n§3> §a").append(cmd.getName());
                            if (cmd.arguments.length != 0) {
                                usage.append(" §3-");
                                for (var argument : cmd.arguments) {
                                    if (argument.isNeeded()) {
                                        usage.append(" §8<§7").append(argument.getName()).append("§8>");
                                    } else {
                                        usage.append(" §8[§7").append(argument.getName()).append("§8]");
                                    }
                                }
                            }
                        }
                    }
                }
                return usage.toString();
            }
            return "\n§cThis command has no Sub commands";
        }
        return BukkitVersion.version().commandApiUtils().unknownCommandMessage();
    }

    public final boolean hasPermission(CommandSender sender) {
        return hasPermission(sender, getPermission());
    }

    public final boolean hasPermission(CommandSender sender, String permission) {
        if (sender.isOp() || permission == null) return true;
        for (var perminfo : sender.getEffectivePermissions()) {
            if (perminfo.getPermission().startsWith(permission) || perminfo.getPermission().equals("*")) return true;
        }
        return false;
    }

    public final void sendUsage(CommandSender sender) {
        sender.sendMessage(getUsage(sender));
    }

    public final void sendUsage(CommandSender sender, String start) {
        sender.sendMessage(getUsage(sender, start));
    }

    public final String getName() {
        return name;
    }

    public final String getPermission() {
        return permission;
    }

    protected final void setPositions(CommandPosition owner) {
        var cpos = owner.next();
        this.pos = cpos;
        for (var child : childs) {
            child.setPositions(cpos);
        }
    }

    public final Set<Command> getChilds() {
        return new HashSet<>(childs);
    }

    public final Set<Command> getAllChilds() {
        Set<Command> childs = new HashSet<>(this.childs);
        for (var child : this.childs) {
            childs.addAll(child.getAllChilds());
        }
        return childs;
    }

    protected CommandPosition getPosition() {
        return pos;
    }

    public final List<String> getSolutions(CommandSender sender) {
        return value(sender, value(childs), "");
    }

    public final boolean hasSubCommands() {
        return !childs.isEmpty();
    }

    public abstract boolean execute(CommandSender sender, String[] args);
}
