package eu.darkcube.system.impl.minestom.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.empty;
import static eu.darkcube.system.server.item.ItemBuilder.item;
import static net.minestom.server.item.Material.*;

import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemBuilder;

public class MinestomItemTemplateItemProvider implements DarkCubeItemTemplates.ItemProvider {
    @Override
    public ItemBuilder provide(char character) {
        return switch (character) {
            case 'l' -> item(GRAY_STAINED_GLASS_PANE).displayname(empty());
            case 'd' -> item(BLACK_STAINED_GLASS_PANE).displayname(empty());
            case 'p' -> item(PURPLE_STAINED_GLASS_PANE).displayname(empty());
            case 'm' -> item(MAGENTA_STAINED_GLASS_PANE).displayname(empty());
            default -> throw new IllegalStateException("Not supported: \"" + character + "\"");
        };
    }
}