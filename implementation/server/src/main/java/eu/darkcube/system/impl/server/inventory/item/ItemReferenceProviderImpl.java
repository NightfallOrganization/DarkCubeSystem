/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;

public class ItemReferenceProviderImpl implements ItemReferenceProvider {
    @Override
    public @NotNull ItemReferenceImpl createFor(@NotNull Object item) {
        return new ItemReferenceImpl(item);
    }
}
