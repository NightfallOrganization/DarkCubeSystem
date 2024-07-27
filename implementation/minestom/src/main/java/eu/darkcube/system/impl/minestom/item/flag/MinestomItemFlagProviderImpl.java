/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.flag;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.flag.MinestomItemFlag;
import eu.darkcube.system.minestom.item.flag.MinestomItemFlagProvider;
import eu.darkcube.system.server.item.flag.ItemFlag;

public class MinestomItemFlagProviderImpl implements MinestomItemFlagProvider {
    private int id = 0;

    @Override
    public @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        if (platformItemFlag instanceof ItemFlag itemFlag) return itemFlag;
        throw new IllegalArgumentException("Invalid ItemFlag: " + platformItemFlag);
    }

    @Override
    public MinestomItemFlag create() {
        return new MinestomItemFlagImpl(id++);
    }
}
