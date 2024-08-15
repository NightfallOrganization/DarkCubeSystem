/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.ItemBlockState;
import net.minecraft.world.item.component.BlockItemStateProperties;

public record BlockStateMapper() implements Mapper<ItemBlockState, BlockItemStateProperties> {
    @Override
    public BlockItemStateProperties apply(ItemBlockState mapping) {
        return new BlockItemStateProperties(mapping.properties());
    }

    @Override
    public ItemBlockState load(BlockItemStateProperties mapping) {
        return new ItemBlockState(mapping.properties());
    }
}
