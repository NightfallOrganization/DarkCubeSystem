/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.enchant;

import java.util.Collection;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public interface Enchantment {
    static @NotNull Enchantment of(@NotNull Object platformEnchantment) {
        return EnchantmentProviderImpl.of(platformEnchantment);
    }

    @NotNull
    @Unmodifiable
    static Collection<Enchantment> values() {
        return EnchantmentProviderImpl.enchantments();
    }

    @NotNull
    Key key();

    int maxLevel();

    default int startLevel() {
        return 1;
    }
}
