/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item;

import eu.darkcube.system.impl.server.item.KeyProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomKeyProvider implements KeyProvider {
    @Override
    public @NotNull Key of(@NotNull Object key) {
        return switch (key) {
            case Key k -> k;
            case Keyed k -> k.key();
            case net.kyori.adventure.key.Key k -> Key.key(k.namespace(),k.value());
            case net.kyori.adventure.key.Keyed k -> of(k.key());
            default -> throw new IllegalArgumentException("Invalid key: " + key);
        };
    }
}
