/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static net.minestom.server.item.Material.*;

import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemBuilder;

public class MinestomItemTemplateItemProvider implements DarkCubeItemTemplates.ItemProvider {
    @Override
    public ItemBuilder provide(char character) {
        return switch (character) {
            case 'l' -> item(GRAY_STAINED_GLASS_PANE);
            case 'd' -> item(BLACK_STAINED_GLASS_PANE);
            case 'p' -> item(PURPLE_STAINED_GLASS_PANE);
            case 'm' -> item(MAGENTA_STAINED_GLASS_PANE);
            default -> throw new IllegalStateException("Not supported: \"" + character + "\"");
        };
    }
}
