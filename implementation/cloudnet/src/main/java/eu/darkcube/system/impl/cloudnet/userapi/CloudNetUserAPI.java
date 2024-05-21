/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserAPI;
import eu.darkcube.system.libs.com.github.benmanes.caffeine.cache.LoadingCache;

public abstract class CloudNetUserAPI extends CommonUserAPI {
    public LoadingCache<UUID, CommonUser> userCache() {
        return userCache;
    }
}
