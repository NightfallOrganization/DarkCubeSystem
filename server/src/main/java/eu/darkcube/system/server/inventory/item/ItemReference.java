/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.item;

public interface ItemReference {
    /**
     * @return whether the item is async
     */
    boolean isAsync();

    /**
     * Makes this Item async. Async items will not block the server.
     * This item will be added as soon as the item was calculated.
     */
    void makeAsync();

    /**
     * Makes this Item sync. Sync items will block the server.
     * This item will be added before the inventory is shown.
     */
    void makeSync();
}
