/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components.util;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagTypes;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.BinaryTagSerializer;

public sealed interface BlockTypeFilter permits BlockTypeFilter.Blocks, BlockTypeFilter.Tag {
    BinaryTagSerializer<BlockTypeFilter> SERIALIZER = new BinaryTagSerializer<>() {
        @Override
        public @UnknownNullability BinaryTag write(@UnknownNullability BlockTypeFilter value) {
            return switch (value) {
                case Blocks blocks -> {
                    var builder = ListBinaryTag.builder(BinaryTagTypes.STRING);
                    for (var block : blocks.blocks) {
                        builder.add(StringBinaryTag.stringBinaryTag(block.key().asMinimalString()));
                    }
                    yield builder.build();
                }
                case Tag tag -> StringBinaryTag.stringBinaryTag("#" + tag.tag.asString());
            };
        }

        @Override
        public @UnknownNullability BlockTypeFilter read(@UnknownNullability BinaryTag tag) {
            return switch (tag) {
                case ListBinaryTag list -> {
                    final List<Material> blocks = new ArrayList<>(list.size());
                    for (var binaryTag : list) {
                        if (!(binaryTag instanceof StringBinaryTag string)) continue;
                        blocks.add(Material.of(Key.key(string.value())));
                    }
                    yield new Blocks(blocks);
                }
                case StringBinaryTag string -> {
                    // Could be a tag or a block name depending if it starts with a #
                    final var value = string.value();
                    if (value.startsWith("#")) {
                        yield new Tag(value.substring(1));
                    } else {
                        yield new Blocks(Material.of(Key.key(value)));
                    }
                }
                default -> throw new IllegalArgumentException("Invalid tag type: " + tag.type());
            };
        }
    };

    record Blocks(@NotNull List<Material> blocks) implements BlockTypeFilter {
        public Blocks {
            blocks = List.copyOf(blocks);
        }

        public Blocks(@NotNull Material @NotNull ... blocks) {
            this(List.of(blocks));
        }
    }

    record Tag(@NotNull Key tag) implements BlockTypeFilter {
        public Tag(@NotNull String namespaceId) {
            this(Key.key(namespaceId));
        }
    }
}