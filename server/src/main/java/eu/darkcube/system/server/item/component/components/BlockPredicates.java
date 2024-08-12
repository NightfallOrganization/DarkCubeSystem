/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.server.item.material.Material;

public record BlockPredicates(@NotNull List<BlockPredicate> predicates, boolean showInTooltip) {
    public BlockPredicates {
        predicates = List.copyOf(predicates);
    }

    public record BlockPredicate(@Nullable BlockTypeFilter blocks, @Nullable PropertiesPredicate state, @Nullable CompoundBinaryTag nbt) {
        public BlockPredicate(@NotNull BlockTypeFilter blocks) {
            this(blocks, null, null);
        }

        public BlockPredicate(@NotNull Material @NotNull ... blocks) {
            this(new BlockTypeFilter.Blocks(blocks), null, null);
        }

        public BlockPredicate(@NotNull PropertiesPredicate state) {
            this(null, state, null);
        }

        public BlockPredicate(@NotNull CompoundBinaryTag nbt) {
            this(null, null, nbt);
        }
    }

    public record PropertiesPredicate(@NotNull Map<String, ValuePredicate> properties) {
        public PropertiesPredicate {
            properties = Map.copyOf(properties);
        }

        public sealed interface ValuePredicate {
            record Exact(@Nullable String value) implements ValuePredicate {
            }

            record Range(@Nullable String min, @Nullable String max) implements ValuePredicate {
            }
        }
    }
}
