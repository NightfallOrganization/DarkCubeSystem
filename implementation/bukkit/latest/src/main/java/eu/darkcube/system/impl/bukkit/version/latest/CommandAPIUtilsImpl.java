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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.version.BukkitCommandAPIUtils;
import eu.darkcube.system.impl.bukkit.version.latest.commandapi.CommandConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.PaperCommands;
import io.papermc.paper.command.brigadier.PluginCommandNode;
import io.papermc.paper.command.brigadier.PluginVanillaCommandWrapper;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.CraftCommandMap;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.help.SimpleHelpMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.LoggerFactory;
import org.spigotmc.SpigotConfig;

@SuppressWarnings("UnstableApiUsage")
public class CommandAPIUtilsImpl extends BukkitCommandAPIUtils implements Listener {
    private final Map<Command, Map<PluginCommandNode, CommandNode<CommandSourceStack>>> custom = new HashMap<>();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CommandAPIUtilsImpl.class);
    private volatile boolean requireSync = false;
    private final PaperCommands commands = PaperCommands.INSTANCE;
    private int requireSyncTick = 0;

    public CommandAPIUtilsImpl() {
    }

    private void register(PluginMeta pluginMeta, CommandDispatcher<CommandSourceStack> dispatcher, LiteralCommandNode<CommandSourceStack> paperNode, Command command, eu.darkcube.system.bukkit.commandapi.Commands.CommandEntry.OriginalCommandTree node) {
        var description = command.description();
        var pluginLiteral = new PluginCommandNode(node.source.getName(), pluginMeta, paperNode, description);

        var root = dispatcher.getRoot();
        var oldChild = root.getChild(node.source.getName());
        if (oldChild != null) {
            logger.warn("Overriding previous command: {}, class: {}", oldChild.getName(), oldChild.getClass().getName());
            root.removeCommand(oldChild.getName());
            this.custom.computeIfAbsent(command, cmd -> new HashMap<>()).put(pluginLiteral, oldChild);
        }
        root.addChild(pluginLiteral);
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
        Bukkit.getPluginManager().registerEvents(new PluginListener(), system);
    }

    private class PluginListener implements Listener {
        @EventHandler
        public void handle(PluginDisableEvent event) {
            var plugin = event.getPlugin().getPluginMeta();

            var entries = CommandAPI.instance().getCommands().commandEntries().stream().filter(e -> {
                var meta = getPluginMeta(e.executor());
                return meta == plugin;
            }).toList();

            if (!entries.isEmpty()) {
                for (var entry : entries) {
                    var newEntry = CommandAPI.instance().getCommands().unregister(entry.executor());
                    if (newEntry == entry) {
                        unregister(entry);
                    } else {
                        logger.warn("Wrong entry: {}!={}", newEntry, entry);
                    }
                }

                updateHelpMap();
                requireSync();
            }
        }
    }
    //
    // private PluginMeta queryMeta(PluginCommandNode node) {
    //     try {
    //         var pluginField = PluginCommandNode.class.getDeclaredField("plugin");
    //         pluginField.setAccessible(true);
    //         return (PluginMeta) pluginField.get(node);
    //     } catch (IllegalAccessException | NoSuchFieldException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

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
            registerLegacy(commandMap.getKnownCommands(), plugin, plugincommand);
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
    public void unregister(Commands.CommandEntry entry) {
        var knownCommands = Bukkit.getCommandMap().getKnownCommands();
        var overwrites = custom.remove(entry.executor());
        for (var node : entry.nodes()) {
            var name = node.source.getName();
            unregister(knownCommands, overwrites, name);
        }
    }

    private void updateHelpMap() {
        var helpMap = (SimpleHelpMap) Bukkit.getHelpMap();
        helpMap.clear();
        helpMap.initializeGeneralTopics();
        helpMap.initializeCommands();
    }

    private void unregister(Map<String, org.bukkit.command.Command> knownCommands, @Nullable Map<PluginCommandNode, CommandNode<CommandSourceStack>> overwrites, String name) {
        var cmd = knownCommands.get(name);
        // might actually be null in case a third party like plugman unregistered a command.
        // in that case we still want to restore the previous command.
        if (cmd != null) {
            if (cmd instanceof PluginVanillaCommandWrapper w) {
                if ((Object) w.vanillaCommand instanceof PluginCommandNode pluginNode) {
                    knownCommands.remove(name);
                    if (overwrites != null) {
                        var oldCommand = overwrites.remove(pluginNode);
                        if (oldCommand != null) {
                            logger.warn("Reinstalling command: {}", oldCommand.getName());
                            commands.getDispatcherInternal().getRoot().addChild(oldCommand);
                        }
                    }
                    return;
                }
            }
        }
        if (overwrites != null) {
            for (var entry : overwrites.entrySet()) {
                if (entry.getKey().getLiteral().equals(name)) {
                    var oldCommand = entry.getValue();
                    logger.warn("Reinstalling command: {}", oldCommand.getName());
                    commands.getDispatcherInternal().getRoot().addChild(oldCommand);
                    overwrites.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @Override
    public double[] getEntityBB(Entity entity) {
        Bukkit.getWorlds().getFirst().spawnEntity(null, EntityType.BLOCK_DISPLAY);
        return new double[]{entity.getBoundingBox().getMinX(), entity.getBoundingBox().getMinY(), entity.getBoundingBox().getMinZ(), entity.getBoundingBox().getMaxX(), entity.getBoundingBox().getMaxY(), entity.getBoundingBox().getMaxZ()};
    }

    @Override
    public void register(eu.darkcube.system.bukkit.commandapi.Commands.CommandEntry entry) {
        var command = entry.executor();

        final var prefix = command.prefix().toLowerCase(Locale.ROOT);
        if (prefix.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in prefix!");

        for (var node : entry.nodes()) {
            var name = node.source.getName();
            if (name.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in name!");
        }

        unregister(entry);

        var paperNode = CommandConverter.convertNode(command, command.name());
        var pluginMeta = getPluginMeta(command);

        var dispatcher = commands.getDispatcherInternal();

        for (var node : entry.nodes()) {
            register(pluginMeta, dispatcher, paperNode, command, node);
        }
    }

    private PluginMeta getPluginMeta(Command command) {
        PluginMeta pluginMeta = null;
        var classLoader = command.getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader pluginClassLoader) {
            var plugin = pluginClassLoader.getPlugin();
            if (plugin != null) {
                pluginMeta = plugin.getPluginMeta();
            }
        } else if (classLoader instanceof PaperPluginClassLoader paperPluginClassLoader) {
            pluginMeta = paperPluginClassLoader.getConfiguration();
        }
        if (pluginMeta == null) throw new IllegalStateException("Unable to get PluginMeta of Command");
        return pluginMeta;
    }

    private void registerLegacy(Map<String, org.bukkit.command.Command> known, Plugin plugin, PluginCommand command) {
        var name = command.getName().toLowerCase(Locale.ENGLISH);
        var prefix = plugin.getName().toLowerCase(Locale.ENGLISH);
        List<String> successfulNames = new ArrayList<>();
        registerLegacy(known, name, prefix, false, command, successfulNames);
        for (var alias : command.getAliases()) {
            registerLegacy(known, alias, prefix, true, command, successfulNames);
        }
    }

    private void registerLegacy(Map<String, org.bukkit.command.Command> known, String name, String prefix, boolean alias, org.bukkit.command.Command command, List<String> successfulNames) {
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
