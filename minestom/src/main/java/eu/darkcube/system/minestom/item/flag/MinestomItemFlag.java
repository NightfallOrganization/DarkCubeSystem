/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.item.flag;

import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;

import static eu.darkcube.system.minestom.item.flag.MinestomItemFlag$Access.provider;

public interface MinestomItemFlag extends ItemFlag {
    MinestomItemFlag HIDE_UNBREAKABLE = provider.create();
    MinestomItemFlag HIDE_ENCHANTMENTS = provider.create();
    MinestomItemFlag HIDE_STORED_ENCHANTMENTS = provider.create();
    MinestomItemFlag HIDE_ATTRIBUTE_LIST = provider.create();
    MinestomItemFlag HIDE_DYED_COLOR = provider.create();
}

class MinestomItemFlag$Access {
    static final MinestomItemFlagProvider provider = (MinestomItemFlagProvider) InternalProvider
            .instance()
            .instance(ItemFlagProvider.class);
}
