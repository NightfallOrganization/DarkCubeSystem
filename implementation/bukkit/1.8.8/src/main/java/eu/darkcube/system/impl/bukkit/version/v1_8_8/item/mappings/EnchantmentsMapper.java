/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.util.HashMap;
import java.util.Locale;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantmentsMapper implements Mapper<EnchantmentList> {
    @Override
    public void apply(EnchantmentList mapping, ItemStack item, ItemMeta meta) {
        for (var entry : mapping.enchantments().entrySet()) {
            var enchantment = Enchantment.getByName(entry.getKey().value().toUpperCase(Locale.ROOT));
            meta.addEnchant(enchantment, entry.getValue(), true);
        }
        if (!mapping.showInTooltip()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
    }

    @Override
    public @Nullable EnchantmentList convert(ItemStack item, ItemMeta meta) {
        if (!meta.hasEnchants()) return null;
        var enchantmentMap = new HashMap<Key, Integer>();
        for (var entry : meta.getEnchants().entrySet()) {
            var key = Key.key(entry.getKey().getName().toLowerCase(Locale.ROOT));
            enchantmentMap.put(key, entry.getValue());
        }
        return new EnchantmentList(enchantmentMap, !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
    }
}
