/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.aikar.timings.TimingsManager;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.impl.bukkit.commandapi.InternalCommandTabExecutor;
import eu.darkcube.system.impl.bukkit.version.BukkitCommandAPIUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

public class CommandAPIUtilsImpl extends BukkitCommandAPIUtils {
    private final TabExecutor executor = new InternalCommandTabExecutor();

    public CommandAPIUtilsImpl() {
    }

    @Override public String unknownCommandMessage() {
        return SpigotConfig.unknownCommandMessage;
    }

    @Override public PluginCommand registerLegacy(Plugin plugin, eu.darkcube.system.bukkit.commandapi.deprecated.Command command) {
        try {
            var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            var plugincommand = constructor.newInstance(command.getName(), plugin);
            plugincommand.setAliases(Arrays.asList(command.getAliases()));
            plugincommand.setUsage(command.getSimpleLongUsage());
            plugincommand.setPermission(command.getPermission());
            plugincommand.timings = TimingsManager.getCommandTiming(plugin.getName(), plugincommand);
            var server = (CraftServer) Bukkit.getServer();
            var commandMap = server.getCommandMap();
            var f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            var knownCommands = (Map<String, org.bukkit.command.Command>) f.get(commandMap);
            register(knownCommands, plugin, plugincommand);
            return plugincommand;
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void unregister(String name) {
        try {
            var commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            final var f = commandMap.getClass().getDeclaredField("knownCommands");
            f.setAccessible(true);
            final var knownCommands = (Map<String, org.bukkit.command.Command>) f.get(commandMap);
            knownCommands.remove(name);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override public void unregister(Command command) {
        try {
            var commandMap = Bukkit.getServer().getCommandMap();
            var f = commandMap.getClass().getDeclaredField("knownCommands");
            f.setAccessible(true);
            final var knownCommands = (Map<String, org.bukkit.command.Command>) f.get(commandMap);

            var prefix = command.getPrefix().toLowerCase(Locale.ROOT);
            for (var name : command.getNames()) {
                unregister(knownCommands, name.toLowerCase(Locale.ROOT));
                unregister(knownCommands, prefix + ":" + name.toLowerCase(Locale.ROOT));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void unregister(Map<String, org.bukkit.command.Command> knownCommands, String name) {
        var cmd = knownCommands.get(name);
        if (cmd instanceof PluginCommand pcmd) {
            if (pcmd.getExecutor() == executor) {
                knownCommands.remove(name);
            }
        }
    }

    @Override public double[] getEntityBB(Entity entity) {
        var bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        return new double[]{bb.a, bb.b, bb.c, bb.d, bb.e, bb.f};
    }

    @Override public void register(Command command) {
        try {
            final var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            final var name = command.getName().toLowerCase(Locale.ROOT);
            if (name.contains(" ")) {
                throw new IllegalArgumentException("Can't register command with whitespace in name!");
            }
            final var aliases = command.getAliases();
            for (var i = 0; i < aliases.length; i++) {
                aliases[i] = aliases[i].toLowerCase();
            }
            final var plugincommand = constructor.newInstance(name, DarkCubePlugin.systemPlugin());
            plugincommand.setAliases(Arrays.asList(aliases));
            plugincommand.setUsage("/" + name);
            plugincommand.setPermission(command.getPermission());
            plugincommand.setExecutor(this.executor);
            plugincommand.setTabCompleter(this.executor);
            var timings = plugincommand.getClass().getField("timings");
            var timingsOf = Class.forName("co.aikar.timings.Timings").getMethod("of", Plugin.class, String.class);
            timings.set(plugincommand, timingsOf.invoke(null, DarkCubePlugin.systemPlugin(), DarkCubePlugin.systemPlugin().getName() + ":" + name));
            var commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            final var f = commandMap.getClass().getDeclaredField("knownCommands");
            f.setAccessible(true);
            final var knownCommands = (Map<String, org.bukkit.command.Command>) f.get(commandMap);
            final var prefix = command.getPrefix().toLowerCase(Locale.ROOT);
            if (prefix.contains(" ")) {
                throw new IllegalArgumentException("Can't register command with whitespace in prefix!");
            }
            for (var n : command.getNames()) {
                final var registerName = prefix + ":" + n;
                this.checkAmbiguities(registerName, knownCommands, plugincommand);
            }
            for (var n : command.getNames()) {
                this.checkAmbiguities(n, knownCommands, plugincommand);
            }
            if (command.getPermission() != null) {
                if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
                }
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void register(Map<String, org.bukkit.command.Command> known, Plugin plugin, PluginCommand command) {
        var name = command.getName().toLowerCase(Locale.ENGLISH);
        var prefix = plugin.getName().toLowerCase(Locale.ENGLISH);
        List<String> successfulNames = new ArrayList<>();
        register(known, name, prefix, false, command, successfulNames);
        for (var alias : command.getAliases()) {
            register(known, alias, prefix, true, command, successfulNames);
        }
    }

    private void register(Map<String, org.bukkit.command.Command> known, String name, String prefix, boolean alias, org.bukkit.command.Command command, List<String> successfulNames) {
        var key = prefix + ":" + name;
        var work = false;
        if (known.containsKey(key)) {
            var ex = known.get(key);
            DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Failed to register command: Command with that name already exists");
            DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Command: " + key + " - " + ex);
            if (!alias) {
                command.setLabel(key);
            }
        } else {
            work = true;
        }
        if (work) {
            known.put(key, command);
            successfulNames.add(key);
        }
        work = false;
        if (known.containsKey(name)) {
            var ex = known.get(name);
            if (ex instanceof VanillaCommandWrapper) {
                work = true;
            } else {
                DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Failed to register command: Command with that name already exists");
                DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Command: " + name + " - " + ex);
            }
        } else {
            work = true;
        }
        if (work) {
            known.put(name, command);
            successfulNames.add(name);
        }
    }

    private void checkAmbiguities(String registerName, Map<String, org.bukkit.command.Command> knownCommands, PluginCommand command) {
        registerName = registerName.toLowerCase(Locale.ROOT);
        if (knownCommands.containsKey(registerName)) {
            var cmd = knownCommands.get(registerName);
            if (cmd instanceof PluginCommand pluginCommand) {
                if (pluginCommand.getExecutor() == this.executor && pluginCommand.getTabCompleter() == this.executor) {
                    return;
                }
                Bukkit.getConsoleSender().sendMessage("§6Overriding command " + registerName + " from Plugin " + pluginCommand.getPlugin().getName() + "!");
            } else {
                Bukkit.getConsoleSender().sendMessage("§6Overriding command " + registerName + " from type " + cmd.getClass().getSimpleName() + "!");
            }
        }
        knownCommands.put(registerName, command);
    }
}
