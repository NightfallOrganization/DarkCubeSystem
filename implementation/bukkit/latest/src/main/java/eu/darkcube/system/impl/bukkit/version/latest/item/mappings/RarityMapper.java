/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.mappings;

import eu.darkcube.system.impl.bukkit.version.latest.item.Mapper;
import eu.darkcube.system.server.item.ItemRarity;
import net.minecraft.world.item.Rarity;

public record RarityMapper() implements Mapper<ItemRarity, Rarity> {
    @Override
    public Rarity apply(ItemRarity mapping) {
        return switch (mapping) {
            case UNCOMMON -> Rarity.UNCOMMON;
            case COMMON -> Rarity.COMMON;
            case RARE -> Rarity.RARE;
            case EPIC -> Rarity.EPIC;
        };
    }

    @Override
    public ItemRarity load(Rarity mapping) {
        return switch (mapping) {
            case COMMON -> ItemRarity.COMMON;
            case UNCOMMON -> ItemRarity.UNCOMMON;
            case RARE -> ItemRarity.RARE;
            case EPIC -> ItemRarity.EPIC;
        };
    }
}
