/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.item.mappings;

import eu.darkcube.system.impl.bukkit.version.v1_8_8.item.Mapper;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.component.components.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class DyedColorMapper implements Mapper<DyedItemColor> {
    @Override
    public void apply(DyedItemColor mapping, ItemStack item, ItemMeta meta) {
        var leatherArmor = (LeatherArmorMeta) meta;
        leatherArmor.setColor(Color.fromRGB(mapping.color().red(), mapping.color().green(), mapping.color().blue()));
    }

    @Override
    public @Nullable DyedItemColor convert(ItemStack item, ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta leatherArmor)) return null;
        var color = leatherArmor.getColor();
        return new DyedItemColor(TextColor.color(color.getRed(), color.getGreen(), color.getBlue()), true);
    }
}
