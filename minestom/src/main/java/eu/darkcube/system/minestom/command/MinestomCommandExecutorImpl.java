/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import java.util.Collection;
import java.util.Collections;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MinestomCommandExecutorImpl implements MinestomCommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MinestomCommandExecutorImpl.class);
    private final CommandSender sender;
    private final Collection<Audience> audiences;

    public MinestomCommandExecutorImpl(CommandSender sender) {
        this.sender = sender;
        this.audiences = Collections.singleton(MinestomAdventureSupport.adventureSupport().audienceProvider().audience(sender));
    }

    @Override
    public @NotNull String commandPrefix() {
        return sender instanceof Player ? "/" : MinestomCommandExecutor.super.commandPrefix();
    }

    @Override
    public @NotNull CommandSender sender() {
        return sender;
    }

    @Override
    public @NotNull Language language() {
        if (sender instanceof Player player) {
            return UserAPI.instance().user(player.getUuid()).language();
        }
        return Language.DEFAULT;
    }

    @Override
    public void language(@NotNull Language language) {
        if (sender instanceof Player player) {
            UserAPI.instance().user(player.getUuid()).language(language);
        }
        logger.warn("Can't set language of something else than player!");
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return audiences;
    }
}
