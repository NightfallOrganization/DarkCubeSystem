/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal record UserAPIHolder() {
    private static final UserAPI instance = InjectionLayer.boot().instance(UserAPI.class);

    public UserAPIHolder {
        throw new AssertionError();
    }

    static UserAPI instance() {
        return instance;
    }
}
