/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.item.flag;

import static eu.darkcube.system.server.item.component.ItemComponent.*;

import eu.darkcube.system.bukkit.item.flag.BukkitItemFlag;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.server.item.component.components.BlockPredicates;
import eu.darkcube.system.server.item.component.components.EnchantmentList;
import eu.darkcube.system.server.item.component.components.Unbreakable;
import eu.darkcube.system.util.Unit;
import org.bukkit.inventory.ItemFlag;

public record BukkitItemFlagImpl(ItemFlag bukkitType) implements BukkitItemFlag {
    @Override
    public void apply(ItemBuilder builder) {
        switch (bukkitType) {
            case HIDE_ENCHANTS -> builder.set(ENCHANTMENTS, builder.get(ENCHANTMENTS, EnchantmentList.EMPTY).withTooltip(false));
            case HIDE_ATTRIBUTES -> builder.set(ATTRIBUTE_MODIFIERS, builder.get(ATTRIBUTE_MODIFIERS, AttributeList.EMPTY).withTooltip(false));
            case HIDE_UNBREAKABLE -> builder.set(UNBREAKABLE, new Unbreakable(false));
            case HIDE_DESTROYS -> builder.set(CAN_BREAK, builder.get(CAN_BREAK, BlockPredicates.NEVER.withTooltip(false)));
            case HIDE_PLACED_ON -> builder.set(CAN_PLACE_ON, builder.get(CAN_PLACE_ON, BlockPredicates.NEVER.withTooltip(false)));
            case HIDE_ADDITIONAL_TOOLTIP -> builder.set(HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
            case HIDE_DYE -> {
                var color = builder.get(DYED_COLOR);
                if (color == null) break;
                builder.set(DYED_COLOR, color.withTooltip(false));
            }
            case HIDE_ARMOR_TRIM -> {
                var trim = builder.get(TRIM);
                if (trim == null) break;
                builder.set(TRIM, trim.withTooltip(false));
            }
            case HIDE_STORED_ENCHANTS -> builder.set(STORED_ENCHANTMENTS, builder.get(STORED_ENCHANTMENTS, EnchantmentList.EMPTY.withTooltip(false)));
        }
    }
}