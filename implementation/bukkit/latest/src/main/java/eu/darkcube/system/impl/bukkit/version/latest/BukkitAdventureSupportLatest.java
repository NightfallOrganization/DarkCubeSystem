package eu.darkcube.system.impl.bukkit.version.latest;

import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.util.BukkitAdventureSupportImpl;
import eu.darkcube.system.impl.kyori.wrapper.DefaultKyoriAdventureSupport;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BukkitAdventureSupportLatest extends BukkitAdventureSupportImpl implements DefaultKyoriAdventureSupport {
    public BukkitAdventureSupportLatest(DarkCubeSystemBukkit system) {
        super(system);
    }
}
