/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.provider.InternalProvider;

@ApiStatus.Internal
record VersionHolder() {
    private static final Version instance = InternalProvider.instance().instance(Version.class);

    static Version instance() {
        return instance;
    }
}
