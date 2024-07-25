/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.enchant;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.enchant.MinestomEnchantment;
import net.minestom.server.item.enchant.Enchantment;

public record MinestomEnchantmentImpl(@NotNull Enchantment minestomType) implements MinestomEnchantment {
    @Override
    public int maxLevel() {
        return minestomType.maxLevel();
    }
}
