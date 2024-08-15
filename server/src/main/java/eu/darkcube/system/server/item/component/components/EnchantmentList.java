/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;

public record EnchantmentList(@NotNull Map<Key, Integer> enchantments, boolean showInTooltip) {
    public static final EnchantmentList EMPTY = new EnchantmentList(Map.of(), true);

    public EnchantmentList {
        enchantments = Map.copyOf(enchantments);
    }

    public EnchantmentList with(@NotNull Key enchantment, int level) {
        var enchantments = new HashMap<>(this.enchantments);
        enchantments.put(enchantment, level);
        return new EnchantmentList(enchantments, showInTooltip);
    }

    public EnchantmentList with(@NotNull Map<@NotNull Key, @NotNull Integer> enchantments) {
        var e = new HashMap<>(this.enchantments);
        e.putAll(enchantments);
        return new EnchantmentList(e, showInTooltip);
    }

    public EnchantmentList withEnchantments(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments) {
        var e = new HashMap<>(this.enchantments);
        for (var entry : enchantments.entrySet()) e.put(entry.getKey().key(), entry.getValue());
        return new EnchantmentList(e, showInTooltip);
    }

    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        var e = new HashMap<Enchantment, Integer>();
        for (var entry : enchantments.entrySet()) e.put(Enchantment.of(entry.getKey()), entry.getValue());
        return Map.copyOf(e);
    }

    public EnchantmentList remove(@NotNull Key enchantment) {
        var enchantments = new HashMap<>(this.enchantments);
        enchantments.remove(enchantment);
        return new EnchantmentList(enchantments, showInTooltip);
    }

    public EnchantmentList withTooltip(boolean showInTooltip) {
        return new EnchantmentList(enchantments, showInTooltip);
    }
}
