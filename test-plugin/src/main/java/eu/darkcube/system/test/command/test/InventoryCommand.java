/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command.test;

import java.util.Random;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
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
        template.setItem(10, 7, s);
        var container = Container.simple(9);
        container.setAt(7, ItemBuilder.item(Material.STONE));
        container.addListener(new ContainerListener() {
            @Override
            public void onItemAdded(int slot, @NotNull ItemBuilder item, int addAmount) {
                System.out.println("Item add: " + slot + " + " + item.amount());
            }

            @Override
            public void onItemRemoved(int slot, @NotNull ItemBuilder previousItem, int takeAmount) {
                System.out.println("Item remove: " + slot + " - " + previousItem.amount());
            }

            @Override
            public void onItemChanged(int slot, @NotNull ItemBuilder previousItem, @NotNull ItemBuilder newItem) {
                System.out.println("Item change: " + slot + " - " + previousItem.amount() + " + " + newItem.amount());
            }
        });
        template.addContainerFactory(ContainerViewFactory.shared(5, container, (user, view) -> {
            var offset = new Random().nextInt(6) - 3;
            var array = new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23};
            for (var i = 0; i < array.length; i++) {
                array[i] = array[i] + offset;
            }
            view.slots(array);
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
