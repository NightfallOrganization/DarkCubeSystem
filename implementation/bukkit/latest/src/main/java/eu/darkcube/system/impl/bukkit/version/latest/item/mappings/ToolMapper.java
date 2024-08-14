/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import java.util.ArrayList;
import java.util.Optional;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.server.item.material.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record ToolMapper() implements Mapper<Tool, net.minecraft.world.item.component.Tool> {
    @Override
    public net.minecraft.world.item.component.Tool apply(Tool mapping) {
        return new net.minecraft.world.item.component.Tool(mapping.rules().stream().map(r -> new net.minecraft.world.item.component.Tool.Rule(get(r.blocks()), Optional.ofNullable(r.speed()), Optional.ofNullable(r.correctForDrops()))).toList(), mapping.defaultMiningSpeed(), mapping.damagePerBlock());
    }

    @Override
    public Tool load(net.minecraft.world.item.component.Tool mapping) {
        return new Tool(mapping.rules().stream().map(r -> new Tool.Rule(get(r.blocks()), r.speed().orElse(null), r.correctForDrops().orElse(null))).toList(), mapping.defaultMiningSpeed(), mapping.damagePerBlock());
    }

    private static BlockTypeFilter get(HolderSet<Block> set) {
        if (set instanceof HolderSet.Named<Block> named) {
            return new BlockTypeFilter.Tag(named.key().location().toShortLanguageKey());
        } else if (set instanceof HolderSet.Direct<Block> direct) {
            var materials = new ArrayList<Material>();
            for (var holder : direct) {
                materials.add(Material.of(holder.unwrapKey().orElseThrow().location()));
            }
            return new BlockTypeFilter.Blocks(materials);
        } else {
            throw new IllegalArgumentException(set.getClass().getSimpleName());
        }
    }

    private static HolderSet<Block> get(BlockTypeFilter filter) {
        return switch (filter) {
            case BlockTypeFilter.Blocks blocks -> {
                var l = new ArrayList<Holder<Block>>();
                for (var material : blocks.blocks()) {
                    var block = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.parse(material.key().asString())).orElseThrow();
                    l.add(block);
                }
                yield HolderSet.direct(l);
            }
            case BlockTypeFilter.Tag tag -> BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse(tag.tag().asString()))).orElseThrow();
        };
    }
}
