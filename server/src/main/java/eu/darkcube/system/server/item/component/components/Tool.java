/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;

public record Tool(@NotNull List<Rule> rules, float defaultMiningSpeed, int damagePerBlock) {
    public record Rule(@NotNull BlockTypeFilter blocks, @Nullable Float speed, @Nullable Boolean correctForDrops) {
    }
}
