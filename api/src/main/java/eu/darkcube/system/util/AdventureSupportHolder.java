/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal record AdventureSupportHolder() {
    private static final AdventureSupport instance = InjectionLayer.ext().instance(AdventureSupport.class);

    public AdventureSupportHolder {
        throw new AssertionError();
    }

    static AdventureSupport instance() {
        var instance = AdventureSupportHolder.instance;
        if (instance == null) throw new AssertionError("UserAPI not initialized");
        return instance;
    }
}
