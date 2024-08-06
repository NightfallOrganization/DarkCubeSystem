/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class LoadedChunksCommand extends Command {
    public LoadedChunksCommand() {
        super("loadedchunks");

        setCondition((sender, _) -> PermissionProvider.provider().hasPermission(sender, "command.loadedchunks"));
        setDefaultExecutor((sender, _) -> {
            for (var instance : MinecraftServer.getInstanceManager().getInstances()) {
                sender.sendMessage(instance.getDimensionName() + ": " + instance.getChunks().size());
            }
        });
    }
}
