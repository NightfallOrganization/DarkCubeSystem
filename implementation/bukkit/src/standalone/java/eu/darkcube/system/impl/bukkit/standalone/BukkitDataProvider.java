package eu.darkcube.system.impl.bukkit.standalone;

import java.nio.file.Path;

import eu.darkcube.system.impl.standalone.util.data.StandaloneCustomPersistentDataProvider;

public class BukkitDataProvider extends StandaloneCustomPersistentDataProvider {
    @Override
    protected Path dataDirectory() {
        return Path.of("plugins", "DarkCubeSystem");
    }
}
