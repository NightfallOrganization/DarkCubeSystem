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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerInventoryListener implements InventoryListener {
    private final Logger logger = LoggerFactory.getLogger("InventoryAPI");

    @Override
    public void onPreOpen(@NotNull Inventory inventory, @NotNull User user) {
        logger.info("PreOpen");
    }

    @Override
    public void onOpen(@NotNull Inventory inventory, @NotNull User user) {
        logger.info("Open");
    }

    @Override
    public void onOpenAnimationFinished(@NotNull Inventory inventory) {
        logger.info("AnimationFinished");
    }

    @Override
    public void onUpdate(@NotNull Inventory inventory) {
        logger.info("Update");
    }

    @Override
    public void onSlotUpdate(@NotNull Inventory inventory, int slot) {
        logger.info("SlotUpdate {}", slot);
    }

    @Override
    public void onClose(@NotNull Inventory inventory, @NotNull User user) {
        logger.info("Close");
    }

    @Override
    public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        logger.info("Click {} {}", slot, item);
    }
}
