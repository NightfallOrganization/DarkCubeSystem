/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.item;

import eu.darkcube.system.provider.InternalProvider;

class ItemReferenceProviderImpl {
    static final ItemReferenceProvider PROVIDER = InternalProvider.instance().instance(ItemReferenceProvider.class);
}
