/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item.flag;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.flag.ItemFlag;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;

public class BukkitItemFlagProvider implements ItemFlagProvider {
    private final ItemFlag[] flags = EnumConverter.convert(org.bukkit.inventory.ItemFlag.class, ItemFlag.class, BukkitItemFlagImpl::new);

    @Override public @NotNull ItemFlag of(@NotNull Object platformItemFlag) {
        if (platformItemFlag instanceof ItemFlag flag) return flag;
        if (platformItemFlag instanceof org.bukkit.inventory.ItemFlag flag) return flags[flag.ordinal()];
        throw new IllegalArgumentException("Invalid ItemFlag: " + platformItemFlag);
    }
}
