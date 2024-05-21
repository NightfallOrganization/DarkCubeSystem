/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class LoadedChunksCommand extends Command {
    public LoadedChunksCommand() {
        super("loadedchunks");
        setCondition((sender, commandString) -> sender instanceof ConsoleSender || sender.hasPermission("command.loadedchunks") || (sender instanceof Player player && player.getPermissionLevel() >= 2));
        setDefaultExecutor((sender, context) -> {
            for (var instance : MinecraftServer.getInstanceManager().getInstances()) {
                sender.sendMessage(instance.getDimensionName() + ": " + instance.getChunks().size());
            }
        });
    }
}
