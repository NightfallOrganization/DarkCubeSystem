/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.Unbreakable;

public record UnbreakableMapper() implements Mapper<Unbreakable, net.minecraft.world.item.component.Unbreakable> {
    @Override
    public net.minecraft.world.item.component.Unbreakable apply(Unbreakable mapping) {
        return new net.minecraft.world.item.component.Unbreakable(mapping.showInTooltip());
    }

    @Override
    public Unbreakable load(net.minecraft.world.item.component.Unbreakable mapping) {
        return new Unbreakable(mapping.showInTooltip());
    }
}
