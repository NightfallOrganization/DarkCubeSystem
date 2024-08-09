package eu.darkcube.system.impl.bukkit.version.v1_8_8.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.empty;
import static eu.darkcube.system.server.item.ItemBuilder.item;
import static org.bukkit.Material.STAINED_GLASS_PANE;

import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.item.ItemBuilder;

public class BukkitItemTemplateItemProvider implements DarkCubeItemTemplates.ItemProvider {
    @Override
    public ItemBuilder provide(char character) {
        return switch (character) {
            case 'l' -> item(STAINED_GLASS_PANE).damage(7).displayname(empty());
            case 'd' -> item(STAINED_GLASS_PANE).damage(15).displayname(empty());
            case 'p' -> item(STAINED_GLASS_PANE).damage(10).displayname(empty());
            case 'm' -> item(STAINED_GLASS_PANE).damage(2).displayname(empty());
            default -> throw new IllegalArgumentException("Not supported: \"" + character + "\"");
        };
    }
}
