/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import java.util.HashMap;

import eu.darkcube.system.bukkit.item.enchantment.BukkitEnchantment;
import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import net.minecraft.server.v1_8_R3.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantmentsMapper implements Mapper<EnchantmentList> {
    @Override
    public void apply(EnchantmentList mapping, ItemStack item, ItemMeta meta) {
        for (var entry : mapping.enchantments().entrySet()) {
            var enchantment = Enchantment.getByName(entry.getKey().asString());
            var e = eu.darkcube.system.server.item.enchant.Enchantment.of(enchantment);
            var bukkit = ((BukkitEnchantment) e).bukkitType();
            meta.addEnchant(bukkit, entry.getValue(), true);
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
            var key = eu.darkcube.system.server.item.enchant.Enchantment.of(entry.getKey()).key();
            enchantmentMap.put(key, entry.getValue());
        }
        return new EnchantmentList(enchantmentMap, !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
    }
}
