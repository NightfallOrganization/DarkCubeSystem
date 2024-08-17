/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.time.Duration;

import eu.darkcube.system.provider.InternalProvider;

class InventoryAPIUtilsImpl {
    static final InventoryAPIUtils UTILS = InternalProvider.instance().instance(InventoryAPIUtils.class);
    static final Duration DEFAULT_TICK = Duration.ofMillis(50);
}
