/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.flag;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface ItemFlag {
    static @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        return ItemFlagProviderImpl.of(platformItemFlag);
    }

    void apply(ItemBuilder builder);
}
