/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryTemplateSettings {
    /**
     * Gets the {@link InventoryTemplate} these settings are associated with.
     *
     * @return the {@link InventoryTemplate}
     */
    @NotNull
    InventoryTemplate inventoryTemplate();
}
