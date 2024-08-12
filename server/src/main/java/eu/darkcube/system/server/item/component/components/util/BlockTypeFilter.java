/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components.util;

import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;

public sealed interface BlockTypeFilter permits BlockTypeFilter.Blocks, BlockTypeFilter.Tags {
    record Blocks(@NotNull List<Material> blocks) implements BlockTypeFilter {
        public Blocks {
            blocks = List.copyOf(blocks);
        }

        public Blocks(@NotNull Material @NotNull ... blocks) {
            this(List.of(blocks));
        }
    }

    record Tags(@NotNull Key tag) implements BlockTypeFilter {
    }
}