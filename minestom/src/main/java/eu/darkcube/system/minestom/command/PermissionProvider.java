package eu.darkcube.system.minestom.command;

import eu.darkcube.system.server.version.ServerVersion;
import net.minestom.server.command.CommandSender;

public interface PermissionProvider {
    boolean hasPermission(CommandSender sender, String permission);

    static PermissionProvider provider() {
        return PermissionProviderImpl.provider;
    }
}

class PermissionProviderImpl {
    static final PermissionProvider provider = ServerVersion.version().provider().service(PermissionProvider.class);
}