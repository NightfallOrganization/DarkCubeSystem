/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.meta;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.server.item.enchant.Enchantment;

public class EnchantmentStorageBuilderMeta implements BuilderMeta {
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public EnchantmentStorageBuilderMeta() {
    }

    public EnchantmentStorageBuilderMeta(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
    }

    public EnchantmentStorageBuilderMeta enchantments(Map<?, ? extends Number> enchantments) {
        this.enchantments.clear();
        this.enchantments.putAll(Map.ofEntries(enchantments.entrySet().stream().map(entry -> Map.entry(Enchantment.of(entry.getKey()), entry.getValue().intValue())).toArray(Map.Entry[]::new)));
        return this;
    }

    public Map<Enchantment, Integer> enchantments() {
        return enchantments;
    }

    @Override
    public BuilderMeta clone() {
        return new EnchantmentStorageBuilderMeta(enchantments);
    }
}
