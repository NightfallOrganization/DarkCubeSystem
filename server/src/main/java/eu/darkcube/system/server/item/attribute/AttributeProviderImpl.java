/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.attribute;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class AttributeProviderImpl {
    private static final AttributeProvider provider = InjectionLayer.ext().instance(AttributeProvider.class);

    public static @NotNull Attribute of(@NotNull Object platformAttribute) {
        return provider.of(platformAttribute);
    }
}
