/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.flag;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class ItemFlagProviderImpl {
    private static final ItemFlagProvider provider = InjectionLayer.ext().instance(ItemFlagProvider.class);

    public static @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        return provider.of(platformItemFlag);
    }
}
