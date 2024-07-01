/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.paged;

import eu.darkcube.system.impl.server.inventory.InventoryItemHandler;
import eu.darkcube.system.impl.server.inventory.InventoryTemplateImpl;
import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;

public class PaginationItemHandler<PlatformItem, PlatformPlayer> implements InventoryItemHandler<PlatformItem, PlatformPlayer> {
    private final TemplateInventoryImpl<PlatformItem> inventory;
    private final InventoryTemplateImpl<PlatformPlayer> template;

    public PaginationItemHandler(TemplateInventoryImpl<PlatformItem> inventory, InventoryTemplateImpl<PlatformPlayer> template) {
        this.inventory = inventory;
        this.template = template;
    }

    @Override
    public @NotNull TemplateInventoryImpl<PlatformItem> inventory() {
        return inventory;
    }

    @Override
    public void doOpen(@NotNull PlatformPlayer player, @NotNull User user) {

    }
}
