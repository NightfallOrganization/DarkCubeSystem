/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.version;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.version.Version;

@Api public interface ServerVersion extends Version {
    static ServerVersion version() {
        return (ServerVersion) Version.version();
    }

    @Api int protocolVersion();
}
