package eu.darkcube.system.test.command.test;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.container.ContainerViewFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.test.command.BaseCommand;
import org.bukkit.Material;

public class InventoryCommand extends BaseCommand {
    private static final InventoryTemplate template;

    static {
        template = Inventory.createChestTemplate(Key.key("testplugin", "inventory_1"), 27);
        DarkCubeInventoryTemplates.Paged.configure3x9(template);
        var s = ItemBuilder.item(Material.STICK);
        template.setItem(0, 0, s);
        template.setItem(0, 1, s);
        template.setItem(0, 2, s);
        template.addContainerFactory(ContainerViewFactory.simple(5, 9, (user, view) -> {
            view.container().setAt(7, ItemBuilder.item(Material.STONE));
            view.slots(3, 4, 5, 12, 13, 14, 21, 22, 23);
        }));
        // template.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
    }

    public InventoryCommand() {
        super("inventory", b -> b.executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            template.open(player);
            return 0;
        }));
    }
}
