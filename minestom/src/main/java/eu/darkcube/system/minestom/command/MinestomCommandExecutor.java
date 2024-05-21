/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import net.minestom.server.command.CommandSender;

public interface MinestomCommandExecutor extends CommandExecutor, ForwardingAudience {

    static MinestomCommandExecutor create(CommandSender sender) {
        return new MinestomCommandExecutorImpl(sender);
    }

    CommandSender sender();

}
