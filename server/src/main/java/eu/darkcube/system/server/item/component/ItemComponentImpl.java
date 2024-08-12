/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component;

import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.data.component.DataComponent;

class ItemComponentImpl {
    private static final ItemComponentProvider PROVIDER = InternalProvider.instance().instance(ItemComponentProvider.class);

    static <T> DataComponent<T> register(String id) {
        return PROVIDER.create(id);
    }
}
