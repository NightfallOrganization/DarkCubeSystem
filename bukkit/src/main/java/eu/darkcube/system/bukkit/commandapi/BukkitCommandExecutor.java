package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.command.CommandSender;

public interface BukkitCommandExecutor extends CommandExecutor {
    static BukkitCommandExecutor create(CommandSender sender) {
        return BukkitCommandExecutorImpl.create(sender);
    }

    @Override
    default boolean hasPermission(@NotNull String permission) {
        return sender().hasPermission(permission);
    }

    @NotNull
    CommandSender sender();
}
