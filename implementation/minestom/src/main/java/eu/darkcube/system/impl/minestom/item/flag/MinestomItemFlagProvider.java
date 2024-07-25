/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.flag;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;

public class MinestomItemFlagProvider implements ItemFlagProvider {
    @Override
    public @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        if (platformItemFlag instanceof ItemFlag itemFlag) return itemFlag;
        throw new IllegalArgumentException("Invalid ItemFlag: " + platformItemFlag);
    }
}
