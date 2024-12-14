/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.impl.bukkit.version.latest.item.mappings.util.MapperUtil;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minecraft.world.item.component.UseRemainder;

public record UseRemainderWrapper() implements Mapper<ItemBuilder, UseRemainder> {
    @Override
    public UseRemainder apply(ItemBuilder mapping) {
        return new UseRemainder(MapperUtil.convert(mapping));
    }

    @Override
    public ItemBuilder load(UseRemainder mapping) {
        return MapperUtil.convert(mapping.convertInto());
    }
}
