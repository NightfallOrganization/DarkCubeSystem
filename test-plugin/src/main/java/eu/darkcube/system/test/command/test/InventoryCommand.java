/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command.test;

import static eu.darkcube.system.bukkit.commandapi.Commands.literal;

import java.util.Arrays;
import java.util.Random;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
import eu.darkcube.system.server.inventory.container.ContainerViewFactory;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.test.command.BaseCommand;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryCommand extends BaseCommand {
    private static final InventoryTemplate template;
    private static final InventoryTemplate template2;
    private static final InventoryTemplate template3;
    private static final InventoryTemplate template4;

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
        template2 = Inventory.createChestTemplate(Key.key("testplugin", "inventory_2"), 5 * 9);
        template2.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(template2);
        template2.addContainerFactory(ContainerViewFactory.shared(5, container, (user, view) -> {
            view.slotPriority(5);
            view.slots(1, 2, 3, 10, 11, 12, 19, 20, 21);
        }));
        template2.addContainerFactory(ContainerViewFactory.shared(8, container, (user, view) -> {
            view.slotPriority(10);
            view.slots(Arrays.stream(new int[]{1, 2, 3, 10, 11, 12, 19, 20, 21}).map(i -> i + 13).toArray());
        }));
        template3 = Inventory.createChestTemplate(Key.key("testplugin", "inventory_3"), 5 * 9);
        template3.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(template3);
        template3.addContainerFactory(ContainerViewFactory.simple(1, 9, (user, view) -> {
            view.dropItemsOnClose(true);
            view.slots(Arrays.stream(new int[]{1, 2, 3, 10, 11, 12, 19, 20, 21}).map(i -> i + 13).toArray());
        }));
        template4 = Inventory.createTemplate(Key.key("testplugin", "inventory_4"), InventoryType.of(org.bukkit.event.inventory.InventoryType.ANVIL));
        template4.setItem(1, 0, ItemStack.of(Material.STONE));
        template4.setItem(1, 1, ItemStack.of(Material.GRASS_BLOCK));
        template4.setItem(1, 2, ItemStack.of(Material.STICK));
        template4.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                var capabilities = ((InventoryCapabilities.Anvil) inventory.capabilities());
                System.out.println(capabilities.renameText());
                System.out.println(item.buildSafe());
            }
        });
    }

    public InventoryCommand() {
        super("inventory", b -> b.executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            template.open(player);
            return 0;
        }).then(literal("2").executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            template2.open(player);
            return 0;
        })).then(literal("3").executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            template3.open(player);
            return 0;
        })).then(literal("4").executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            template4.open(player);
            return 0;
        })));
    }
}
