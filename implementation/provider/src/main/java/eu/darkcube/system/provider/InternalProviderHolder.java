/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider;

import java.util.ServiceLoader;

class InternalProviderHolder {
    static final InternalProvider PROVIDER = ServiceLoader.load(InternalProvider.class, InternalProviderHolder.class.getClassLoader()).findFirst().orElseThrow();
}
