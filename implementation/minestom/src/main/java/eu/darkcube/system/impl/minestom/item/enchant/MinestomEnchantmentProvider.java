/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.item.enchant;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;

public class MinestomEnchantmentProvider implements EnchantmentProvider {
    private final Map<net.minestom.server.item.Enchantment, Enchantment> enchantments;

    public MinestomEnchantmentProvider() {
        var enchantments = new HashMap<net.minestom.server.item.Enchantment, Enchantment>();
        for (var enchantment : net.minestom.server.item.Enchantment.values()) {
            enchantments.put(enchantment, new MinestomEnchantmentImpl(enchantment));
        }
        this.enchantments = enchantments;
    }

    @Override
    public @NotNull Enchantment of(@NotNull Object platformObject) {
        if (platformObject instanceof Enchantment enchantment) return enchantment;
        if (platformObject instanceof net.minestom.server.item.Enchantment enchantment) return enchantments.get(enchantment);
        throw new IllegalArgumentException("Invalid Enchantment: " + platformObject);
    }
}
