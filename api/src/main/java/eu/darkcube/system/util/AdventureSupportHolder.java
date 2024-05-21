/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.provider.InternalProvider;

@ApiStatus.Internal
record AdventureSupportHolder() {
    private static final AdventureSupport instance = InternalProvider.instance().instance(AdventureSupport.class);

    public AdventureSupportHolder {
        throw new AssertionError();
    }

    static AdventureSupport instance() {
        var instance = AdventureSupportHolder.instance;
        if (instance == null) throw new AssertionError("AdventureSupport not initialized");
        return instance;
    }
}
