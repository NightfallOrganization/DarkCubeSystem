/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class EquipmentSlotProviderImpl {
    private static final EquipmentSlotProvider provider = InjectionLayer.ext().instance(EquipmentSlotProvider.class);

    static @NotNull EquipmentSlot of(@NotNull Object platformObject) {
        return provider.of(platformObject);
    }
}
