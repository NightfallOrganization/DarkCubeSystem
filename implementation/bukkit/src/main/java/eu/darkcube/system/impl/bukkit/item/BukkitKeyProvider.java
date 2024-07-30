package eu.darkcube.system.impl.bukkit.item;

import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public abstract class BukkitKeyProvider implements KeyProvider {
    @Override
    public @NotNull Key of(@NotNull Object key) {
        if (key instanceof Key k) return k;
        if (key instanceof Keyed k) return k.key();
        var k = of0(key);
        if (k != null) return k;
        throw new IllegalArgumentException("Bad key: " + key);
    }

    protected abstract @Nullable Key of0(@NotNull Object key);
}
