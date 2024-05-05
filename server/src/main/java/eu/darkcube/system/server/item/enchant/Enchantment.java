/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.enchant;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

public interface Enchantment {

    static @UnknownNullability Enchantment of(@NotNull Object platformEnchantment) {
        return EnchantmentProviderImpl.of(platformEnchantment);
    }

    int maxLevel();

    default int startLevel() {
        return 1;
    }
}
