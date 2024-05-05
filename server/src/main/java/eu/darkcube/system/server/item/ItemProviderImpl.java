/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item;

import eu.cloudnetservice.driver.inject.InjectionLayer;

class ItemProviderImpl {
    private static final ItemProvider provider = InjectionLayer.ext().instance(ItemProvider.class);

    static ItemProvider itemProvider() {
        return provider;
    }
}
