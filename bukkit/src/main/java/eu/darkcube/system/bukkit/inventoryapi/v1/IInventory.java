/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.inventoryapi.v1;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public interface IInventory {

    static int index(int slot) {
        return slot % 9;
    }

    static int row(int slot) {
        return slot / 9 + 1;
    }

    static int slot(int row, int index) {
        return (row - 1) * 9 + index - 1;
    }

    static int slot0(int index, int row) {
        return slot(row, index);
    }

    static int[] rowAndIndex(int slot) {
        return new int[]{row(slot), index(slot)};
    }

    static double distance(int slot1, int slot2) {
        int[] ri = rowAndIndex(slot1);
        int r1 = ri[0];
        int i1 = ri[1];
        ri = rowAndIndex(slot2);
        int r2 = ri[0];
        int i2 = ri[1];
        return distance(r1, i1, r2, i2);
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }

    static double distanceSquared(double x1, double y1, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    InventoryType getType();

    Inventory getHandle();

    void open(HumanEntity player);

    boolean isOpened(HumanEntity player);
}
