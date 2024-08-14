/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.BinaryTagSerializer;

public record BlockPredicates(@NotNull List<BlockPredicate> predicates, boolean showInTooltip) {
    public static final BlockPredicates NEVER = new BlockPredicates(List.of(), false);

    public static final BinaryTagSerializer<BlockPredicates> SERIALIZER = new BinaryTagSerializer<>() {
        private static final BinaryTagSerializer<List<BlockPredicate>> LIST_TYPE = BlockPredicate.SERIALIZER.list();

        @Override
        public @UnknownNullability BinaryTag write(@UnknownNullability BlockPredicates value) {
            return CompoundBinaryTag.builder().put("predicates", LIST_TYPE.write(value.predicates)).putBoolean("show_in_tooltip", value.showInTooltip).build();
        }

        @Override
        public @UnknownNullability BlockPredicates read(@UnknownNullability BinaryTag tag) {
            if (!(tag instanceof CompoundBinaryTag compound)) return NEVER;
            List<BlockPredicate> predicates;
            var predicatesTag = compound.get("predicates");
            if (predicatesTag != null) {
                predicates = LIST_TYPE.read(tag);
            } else {
                predicates = List.of(BlockPredicate.SERIALIZER.read(tag));
            }
            var showInTooltip = compound.getBoolean("show_in_tooltip", true);
            return new BlockPredicates(predicates, showInTooltip);
        }
    };

    public BlockPredicates {
        predicates = List.copyOf(predicates);
    }

    public record BlockPredicate(@Nullable BlockTypeFilter blocks, @Nullable PropertiesPredicate state, @Nullable CompoundBinaryTag nbt) {
        public static final BlockPredicate ALL = new BlockPredicate(null, null, null);
        public static final BinaryTagSerializer<BlockPredicate> SERIALIZER = new BinaryTagSerializer<>() {
            @Override
            public @UnknownNullability BinaryTag write(@UnknownNullability BlockPredicate value) {
                var b = CompoundBinaryTag.builder();
                if (value.blocks != null) b.put("blocks", BlockTypeFilter.SERIALIZER.write(value.blocks));
                if (value.state != null) b.put("state", PropertiesPredicate.SERIALIZER.write(value.state));
                if (value.nbt != null) b.put("nbt", value.nbt);
                return b.build();
            }

            @Override
            public @UnknownNullability BlockPredicate read(@UnknownNullability BinaryTag tag) {
                if (!(tag instanceof CompoundBinaryTag compound)) return BlockPredicate.ALL;
                BinaryTag entry;
                BlockTypeFilter blocks = null;
                if ((entry = compound.get("blocks")) != null) blocks = BlockTypeFilter.SERIALIZER.read(entry);
                PropertiesPredicate state = null;
                if ((entry = compound.get("state")) != null) state = PropertiesPredicate.SERIALIZER.read(entry);
                CompoundBinaryTag nbt = null;
                if ((entry = compound.get("nbt")) != null) nbt = BinaryTagSerializer.COMPOUND_COERCED.read(entry);
                return new BlockPredicate(blocks, state, nbt);
            }
        };

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
        public static final BinaryTagSerializer<PropertiesPredicate> SERIALIZER = BinaryTagSerializer.COMPOUND.map(tag -> {
            var properties = new HashMap<String, ValuePredicate>();
            for (var entry : tag) {
                properties.put(entry.getKey(), ValuePredicate.SERIALIZER.read(entry.getValue()));
            }
            return new PropertiesPredicate(properties);
        }, value -> {
            var b = CompoundBinaryTag.builder();
            for (var entry : value.properties.entrySet()) {
                b.put(entry.getKey(), ValuePredicate.SERIALIZER.write(entry.getValue()));
            }
            return b.build();
        });

        public PropertiesPredicate {
            properties = Map.copyOf(properties);
        }

        public sealed interface ValuePredicate {
            BinaryTagSerializer<ValuePredicate> SERIALIZER = new BinaryTagSerializer<>() {
                @Override
                public @UnknownNullability BinaryTag write(@UnknownNullability ValuePredicate value) {
                    return switch (value) {
                        case Exact exact -> Exact.SERIALIZER.write(exact);
                        case Range range -> Range.SERIALIZER.write(range);
                    };
                }

                @Override
                public @UnknownNullability ValuePredicate read(@UnknownNullability BinaryTag tag) {
                    if (tag instanceof StringBinaryTag) {
                        return Exact.SERIALIZER.read(tag);
                    } else {
                        return Range.SERIALIZER.read(tag);
                    }
                }
            };

            record Exact(@Nullable String value) implements ValuePredicate {
                public static final BinaryTagSerializer<Exact> SERIALIZER = BinaryTagSerializer.STRING.map(Exact::new, Exact::value);
            }

            record Range(@Nullable String min, @Nullable String max) implements ValuePredicate {
                public static final BinaryTagSerializer<Range> SERIALIZER = BinaryTagSerializer.COMPOUND.map(tag -> new Range(tag.get("min") instanceof StringBinaryTag string ? string.value() : null, tag.get("max") instanceof StringBinaryTag string ? string.value() : null), value -> {
                    var b = CompoundBinaryTag.builder();
                    if (value.min != null) b.putString("min", value.min);
                    if (value.max != null) b.putString("max", value.max);
                    return b.build();
                });
            }
        }
    }
}
