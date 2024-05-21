/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.version.BukkitCommandAPIUtils;
import eu.darkcube.system.impl.bukkit.version.latest.commandapi.CommandConverter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_20_R2.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

public class CommandAPIUtilsImpl extends BukkitCommandAPIUtils implements Listener {
    private final Map<VanillaCommandWrapper, VanillaCommandWrapper> custom = new HashMap<>();
    private volatile boolean requireSync = false;
    private int requireSyncTick = 0;

    private static VanillaCommandWrapper[] wrappers(Command command) {
        final var prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        if (prefix.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in prefix!");

        for (var name : command.getNames()) {
            if (name.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in name!");
        }

        var converter = new CommandConverter(command);
        return converter.convert();
    }

    private void requireSync() {
        requireSync = true;
        requireSyncTick = MinecraftServer.getServer().getTickCount();
    }

    public void enabled(DarkCubeSystemBukkit system) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (requireSync) {
                    if (requireSyncTick + 10 >= MinecraftServer.getServer().getTickCount()) return;
                    requireSync = false;
                    ((CraftServer) Bukkit.getServer()).syncCommands();
                }
            }
        }.runTaskTimer(system, 1, 1);
    }

    @Override
    public String unknownCommandMessage() {
        return SpigotConfig.unknownCommandMessage;
    }

    @Override
    public PluginCommand registerLegacy(Plugin plugin, eu.darkcube.system.bukkit.commandapi.deprecated.Command command) {
        try {
            var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            var plugincommand = constructor.newInstance(command.getName(), plugin);
            plugincommand.setAliases(Arrays.asList(command.getAliases()));
            plugincommand.setUsage(command.getSimpleLongUsage());
            plugincommand.setPermission(command.getPermission());
            var server = (CraftServer) Bukkit.getServer();
            var commandMap = (CraftCommandMap) server.getCommandMap();
            register(commandMap.getKnownCommands(), plugin, plugincommand);
            return plugincommand;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String name) {
        var knownCommands = Bukkit.getServer().getCommandMap().getKnownCommands();
        knownCommands.remove(name);
    }

    @Override
    public void unregister(Command command) {
        var knownCommands = Bukkit.getCommandMap().getKnownCommands();
        var prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        for (var name : command.getNames()) {
            unregister(knownCommands, name.toLowerCase(Locale.ROOT));
            unregister(knownCommands, prefix + ":" + name.toLowerCase(Locale.ROOT));
        }
        requireSync();
    }

    private void unregister(Map<String, org.bukkit.command.Command> knownCommands, String name) {
        var cmd = knownCommands.get(name);
        if (!(cmd instanceof VanillaCommandWrapper w)) return;
        if (!custom.containsKey(w)) return;
        var wrapper = custom.remove(w);
        if (wrapper == w) wrapper = null;
        knownCommands.remove(name);
        for (var node : new ArrayList<>(MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().getChildren())) {
            if (node.getName().equals(name)) {
                MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().getChildren().remove(node);
                if (wrapper != null) {
                    Logger.getLogger("CommandAPI").warning("Reinstalling command: " + wrapper.getName());
                    knownCommands.put(node.getName(), wrapper);
                    MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().addChild(wrapper.vanillaCommand);
                }
                break;
            }
        }
    }

    @Override
    public double[] getEntityBB(Entity entity) {
        return new double[]{entity.getBoundingBox().getMinX(), entity.getBoundingBox().getMinY(), entity.getBoundingBox().getMinZ(), entity.getBoundingBox().getMaxX(), entity.getBoundingBox().getMaxY(), entity.getBoundingBox().getMaxZ()};
    }

    @Override
    public void register(Command command) {
        try {
            unregister(command);
            var wrappers = wrappers(command);

            var mcd = MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher();

            for (var wrapper : wrappers) {
                var it = mcd.getRoot().getChildren().iterator();
                while (it.hasNext()) {
                    var node = it.next();
                    if (node.getName().equals(wrapper.getName())) {
                        var cmd = Bukkit.getCommandMap().getKnownCommands().get(node.getName());
                        if (cmd == null) {
                            Bukkit.getCommandMap().getKnownCommands().put(node.getName(), new VanillaCommandWrapper(MinecraftServer.getServer().vanillaCommandDispatcher, node));
                        }
                        it.remove();
                    }
                }
                var cmd = Bukkit.getCommandMap().getKnownCommands().put(wrapper.getName(), wrapper);

                if (cmd != null) {
                    Logger.getLogger("CommandAPI").warning("Overridden command: " + cmd.getName() + " (" + cmd.getClass().getSimpleName() + ")");
                    if (cmd instanceof VanillaCommandWrapper w) {
                        custom.put(wrapper, w);
                    } else {
                        custom.put(wrapper, wrapper);
                    }
                } else {
                    custom.put(wrapper, wrapper);
                }
                mcd.getRoot().addChild(wrapper.vanillaCommand);
            }
            if (command.getPermission() != null) {
                if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
                }
            }
            requireSync();
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
}
