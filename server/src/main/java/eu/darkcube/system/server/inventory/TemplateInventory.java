/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.server.inventory.controller.PagedInventoryController;

/**
 * Represents an inventory created from an {@link InventoryTemplate}
 */
public interface TemplateInventory extends Inventory {
    @Api
    PagedInventoryController pagedController();
}
