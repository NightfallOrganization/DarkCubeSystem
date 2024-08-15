/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantmentGlintOverrideMapper implements Mapper.Complex<Boolean> {
    @Override
    public void apply(Boolean mapping, ItemStack item, ItemMeta meta, ItemBuilder builder) {
        // Can't override item not to glint in 1.8.8
        if (mapping) {
            var enchants = builder.get(ItemComponent.ENCHANTMENTS);
            if (enchants == null || enchants.enchantments().isEmpty()) {
                meta.addEnchant(item.getType() == Material.BOW ? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
    }

    @Override
    public @Nullable Boolean convert(ItemStack item, ItemMeta meta) {
        // Nothing to do here, this is done via enchantments
        return null;
    }
}
