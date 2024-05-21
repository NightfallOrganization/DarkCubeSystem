/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop", "exit");
        setCondition((sender, commandString) -> sender.hasPermission("command.stop"));
        setDefaultExecutor((sender, context) -> {
            MinecraftServer.stopCleanly();

            System.exit(0);
        });
    }
}
