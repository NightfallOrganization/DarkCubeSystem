/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.Namespaced;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PluginPersistentDataProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Plugin extends JavaPlugin implements Namespaced {

    private static HashMap<YamlConfiguration, File> fileFromConfig = new HashMap<>();
    private static HashMap<String, YamlConfiguration> configFromName = new HashMap<>();
    private final PersistentDataStorage storage;
    private final Key key;

    public Plugin(String pluginName) {
        this(Key.key(pluginName, pluginName));
    }

    public Plugin(Key key) {
        this.storage = PluginPersistentDataProvider.pluginPersistentDataProvider().persistentData(key);
        this.key = key;
    }

    @Override
    public @NotNull String namespace() {
        return getName();
    }

    public PersistentDataStorage persistentDataStorage() {
        return storage;
    }

    public Key key() {
        return key;
    }

    public abstract String getCommandPrefix();

    public Plugin saveDefaultConfig(String path) {
        path += ".yml";
        var f = new File(this.getDataFolder().getPath() + "/" + path);
        if (!f.exists()) {
            this.saveResource(path, true);
        }
        return this;
    }

    public void createConfig(String name) {
        name += ".yml";
        var f = new File(this.getDataFolder().getPath() + "/" + name);
        var cfg = YamlConfiguration.loadConfiguration(f);
        Plugin.fileFromConfig.put(cfg, f);
        Plugin.configFromName.put(name, cfg);

        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig(String name) {
        name += ".yml";
        return Plugin.configFromName.get(name);
    }

    public Plugin saveConfig(YamlConfiguration cfg) {
        try {
            cfg.save(Plugin.fileFromConfig.get(cfg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public YamlConfiguration reloadConfig(String name) {
        name += ".yml";
        var f = new File(this.getDataFolder().getPath() + "/" + name);
        Plugin.fileFromConfig.remove(Plugin.configFromName.get(name));
        Plugin.configFromName.remove(name);
        var cfg = YamlConfiguration.loadConfiguration(f);
        Plugin.configFromName.put(name, cfg);
        Plugin.fileFromConfig.put(cfg, f);
        return cfg;
    }

    public final void sendConsole(String msg, String iprefix) {
        this.sendMessageBridge(("§7> " + this.getCommandPrefix() + ("§6 " + iprefix + " §7| ").replace("  ", " ") + msg).replace("§r", "§r§7").replace("§f", "§7"), Bukkit.getConsoleSender());
    }

    public final void sendConsole(String msg) {
        this.sendConsole(msg, "");
    }

    public final void sendMessage(String msg) {
        this.sendMessageBridgePrefix(msg);
    }

    public final void sendMessageWithoutPrefix(String msg, Collection<? extends CommandSender> receivers) {
        receivers.forEach(r -> this.sendMessageBridge(msg, r));
    }

    public final void sendConsoleWithoutPrefix(String msg) {
        this.sendMessageBridge(msg, Bukkit.getConsoleSender());
    }

    public final void sendMessageWithoutPrefix(String msg, CommandSender sender) {
        sender.sendMessage(msg);
    }

    public final void sendMessage(String msg, Collection<? extends CommandSender> receivers) {
        receivers.forEach(r -> this.sendMessage(msg, r));
    }

    public final void sendMessage(String msg, CommandSender sender) {
        this.sendMessageBridgePrefix(msg, sender);
    }

    private void sendMessageBridgePrefix(String msg, CommandSender sender) {
        if (sender instanceof Player) {
            msg = "§7» §8[§6" + this.getCommandPrefix() + "§8] §7┃ " + msg;
        } else if (sender instanceof ConsoleCommandSender) {
            this.sendConsole(msg);
            return;
        }
        this.sendMessageBridge(msg, sender);
    }

    private void sendMessageBridgePrefix(String msg) {
        for (var p : Bukkit.getOnlinePlayers()) {
            this.sendMessageBridgePrefix(msg, p);
        }
        this.sendMessageBridgePrefix(msg, Bukkit.getConsoleSender());
    }

    private void sendMessageBridge(String msg, CommandSender sender) {
        this.sendMessageWithoutPrefix(msg, sender);
    }

}
