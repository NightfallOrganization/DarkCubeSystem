/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import eu.darkcube.system.provider.InternalProvider;

class InventoryProviderImpl {
    private static final InventoryProvider provider = InternalProvider.instance().instance(InventoryProvider.class);

    static InventoryProvider inventoryProvider() {
        return provider;
    }
}
