/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.bukkit.util.BukkitAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BukkitCommandExecutorImpl implements BukkitCommandExecutor, ForwardingAudience.Single {

    private static final Logger logger = LoggerFactory.getLogger("System");
    private final CommandSender sender;
    private final Audience audience;

    private BukkitCommandExecutorImpl(CommandSender sender) {
        this.sender = sender;
        var s = sender;
        while (s instanceof ProxiedCommandSender) s = ((ProxiedCommandSender) s).getCaller();
        this.audience = BukkitAdventureSupport.adventureSupport().audienceProvider().sender(s);

        Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
    }

    public static BukkitCommandExecutorImpl create(CommandSender sender) {
        return new BukkitCommandExecutorImpl(sender);
    }

    @NotNull
    @Override
    public Audience audience() {
        return audience;
    }

    @Override
    public @NotNull Language language() {
        if (sender instanceof Player) {
            return UserAPI.instance().user(((Player) sender).getUniqueId()).language();
        }
        return Language.DEFAULT;
    }

    @Override
    public void language(@NotNull Language language) {
        if (sender instanceof Player) {
            UserAPI.instance().user(((Player) sender).getUniqueId()).language(language);
            return;
        }
        logger.warn("Can't set language of the console!");
    }

    @Override
    public @NotNull String commandPrefix() {
        return sender instanceof Player ? "/" : BukkitCommandExecutor.super.commandPrefix();
    }

    @Override
    public @NotNull CommandSender sender() {
        return sender;
    }
}
