/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item.enchant;

import eu.darkcube.system.bukkit.item.enchantment.BukkitEnchantment;
import org.bukkit.enchantments.Enchantment;

public record BukkitEnchantmentImpl(Enchantment bukkitType) implements BukkitEnchantment {
    @Override public int maxLevel() {
        return bukkitType.getMaxLevel();
    }
}
