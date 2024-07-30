package eu.darkcube.system.impl.bukkit.version.latest.item;

import eu.darkcube.system.impl.bukkit.item.BukkitKeyProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.kyori.adventure.key.Keyed;

public class KeyProviderImpl extends BukkitKeyProvider {
    @Override
    protected @Nullable Key of0(@NotNull Object key) {
        if (key instanceof Keyed k) key = k.key();
        if (key instanceof net.kyori.adventure.key.Key k) return Key.key(k.namespace(), k.value());
        return null;
    }
}
