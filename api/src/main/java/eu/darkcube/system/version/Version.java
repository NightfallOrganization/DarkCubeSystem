/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.provider.Provider;

/**
 * Obtain an instance with {@link #version()}.
 */
@Api public interface Version {

    static Version version() {
        return VersionHolder.instance();
    }

    @Api Provider provider();

}
