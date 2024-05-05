/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.listener;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class LoggerInventoryListener implements InventoryListener {
    @Override
    public void onPreOpen(@NotNull Inventory inventory, @NotNull User user) {
        System.out.println("PreOpen");
    }

    @Override
    public void onOpen(@NotNull Inventory inventory, @NotNull User user) {
        System.out.println("Open");
    }

    @Override
    public void onOpenAnimationFinished(@NotNull Inventory inventory) {
        System.out.println("AnimationFinished");
    }

    @Override
    public void onUpdate(@NotNull Inventory inventory) {
        System.out.println("Update");
    }

    @Override
    public void onSlotUpdate(@NotNull Inventory inventory, int slot) {
        System.out.println("SlotUpdate " + slot);
    }

    @Override
    public void onClose(@NotNull Inventory inventory, @NotNull User user) {
        System.out.println("Close");
    }

    @Override
    public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        System.out.println("Click " + slot + " " + item);
    }
}
