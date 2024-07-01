/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.item.enchant;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;

public class BukkitEnchantmentProvider implements EnchantmentProvider {
    private Map<Integer, Enchantment> enchantments;

    private Map<Integer, Enchantment> enchantments() {
        if (this.enchantments != null) return this.enchantments;
        this.enchantments = new HashMap<>();
        for (var bukkitEnchantment : org.bukkit.enchantments.Enchantment.values()) {
            var enchantment = new BukkitEnchantmentImpl(bukkitEnchantment);
            var id = bukkitEnchantment.hashCode();
            enchantments.put(id, enchantment);
        }
        return this.enchantments;
    }

    @NotNull
    @Override
    public Enchantment of(@NotNull Object platformObject) {
        if (platformObject instanceof Enchantment enchantment) return enchantment;
        if (platformObject instanceof org.bukkit.enchantments.Enchantment enchantment) {
            var e = this.enchantments().get(enchantment.hashCode());
            if (e == null) throw new IllegalArgumentException();
            return e;
        }
        throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
    }
}
