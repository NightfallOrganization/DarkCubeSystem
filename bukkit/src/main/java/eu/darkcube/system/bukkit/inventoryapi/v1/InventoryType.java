/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.inventoryapi.v1;

import java.util.HashMap;
import java.util.Map;

public final class InventoryType {

    private final String id;

    private static final Map<String, InventoryType> TYPES = new HashMap<>();

    private InventoryType(String id) {
        this.id = id;
    }

    public static InventoryType of(String id) {
        if (!TYPES.containsKey(id)) {
            TYPES.put(id, new InventoryType(id));
        }
        return TYPES.get(id);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
