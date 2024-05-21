/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import java.util.Collections;
import java.util.logging.Logger;

import eu.darkcube.system.bukkit.util.BukkitAdventureSupport;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;

public class BukkitCommandExecutor implements CommandExecutor, ForwardingAudience {

    private static final Logger logger = Logger.getLogger("System");
    private final CommandSender sender;
    private final Audience audience;

    private BukkitCommandExecutor(CommandSender sender) {
        this.sender = sender;
        var s = sender;
        while (s instanceof ProxiedCommandSender) s = ((ProxiedCommandSender) s).getCaller();
        this.audience = BukkitAdventureSupport.adventureSupport().audienceProvider().sender(s);
        Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
    }

    public static BukkitCommandExecutor create(CommandSender sender) {
        return new BukkitCommandExecutor(sender);
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return Collections.singleton(audience);
    }

    @Override
    public Language language() {
        if (sender instanceof Player) {
            return UserAPI.instance().user(((Player) sender).getUniqueId()).language();
        }
        return Language.DEFAULT;
    }

    @Override
    public void language(Language language) {
        if (sender instanceof Player) {
            UserAPI.instance().user(((Player) sender).getUniqueId()).language(language);
            return;
        }
        logger.warning("Can't set language of the console!");
    }

    @Override
    public String commandPrefix() {
        return sender instanceof Player ? "/" : CommandExecutor.super.commandPrefix();
    }

    public CommandSender sender() {
        return sender;
    }
}
