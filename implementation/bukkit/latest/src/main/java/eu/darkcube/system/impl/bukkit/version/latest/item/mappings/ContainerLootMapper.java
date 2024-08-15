/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.component.components.SeededContainerLoot;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ContainerLootMapper() implements Mapper<SeededContainerLoot, net.minecraft.world.item.component.SeededContainerLoot> {
    @Override
    public net.minecraft.world.item.component.SeededContainerLoot apply(SeededContainerLoot mapping) {
        return new net.minecraft.world.item.component.SeededContainerLoot(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(mapping.lootTable())), mapping.seed());
    }

    @Override
    public SeededContainerLoot load(net.minecraft.world.item.component.SeededContainerLoot mapping) {
        return new SeededContainerLoot(mapping.lootTable().location().toString(), mapping.seed());
    }
}
