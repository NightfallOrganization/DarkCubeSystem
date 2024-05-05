/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.firework;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class FireworkEffectProviderImpl {
    private static final FireworkEffectProvider provider = InjectionLayer.ext().instance(FireworkEffectProvider.class);

    public static @NotNull FireworkEffect of(@NotNull Object platformFireworkEffect) {
        return provider.of(platformFireworkEffect);
    }
}
