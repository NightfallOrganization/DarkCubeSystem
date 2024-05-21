/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.flag;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import net.minestom.server.item.ItemHideFlag;

public class MinestomItemFlagProvider implements ItemFlagProvider {
    private final ItemFlag[] flags = EnumConverter.convert(ItemHideFlag.class, ItemFlag.class, MinestomItemFlagImpl::new);

    @Override
    public @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        if (platformItemFlag instanceof ItemFlag itemFlag) return itemFlag;
        if (platformItemFlag instanceof ItemHideFlag itemHideFlag) return flags[itemHideFlag.ordinal()];
        throw new IllegalArgumentException("Invalid ItemFlag: " + platformItemFlag);
    }
}
