/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static org.bukkit.Material.STAINED_GLASS_PANE;

import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemBuilder;

public class BukkitItemTemplateItemProvider implements DarkCubeItemTemplates.ItemProvider {
    @Override
    public ItemBuilder provide(char character) {
        return switch (character) {
            case 'l' -> item(STAINED_GLASS_PANE).damage(7);
            case 'd' -> item(STAINED_GLASS_PANE).damage(15);
            case 'p' -> item(STAINED_GLASS_PANE).damage(10);
            case 'm' -> item(STAINED_GLASS_PANE).damage(2);
            default -> throw new IllegalArgumentException("Not supported: \"" + character + "\"");
        };
    }
}
