/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal record VersionHolder() {
    private static final Version instance = InjectionLayer.ext().instance(Version.class);

    static Version instance() {
        return instance;
    }
}
