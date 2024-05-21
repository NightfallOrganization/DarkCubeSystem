/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.enchant;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;

class EnchantmentProviderImpl {
    private static final EnchantmentProvider provider = InternalProvider.instance().instance(EnchantmentProvider.class);

    public static @NotNull Enchantment of(@NotNull Object platformEnchantment) {
        return provider.of(platformEnchantment);
    }
}
