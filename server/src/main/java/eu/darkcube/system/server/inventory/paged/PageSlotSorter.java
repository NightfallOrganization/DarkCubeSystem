/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.paged;

import java.util.Arrays;

/**
 * Sorter used to sort items in a page when they are about to be displayed.
 */
public interface PageSlotSorter {
    /**
     * Sorts the given pageSlots. There may be multiple sorting implementations.
     * The given array must be modified to change the item display order.
     *
     * @param pageSlots the page slots to sort
     */
    void sort(int[] pageSlots);

    enum Sorters implements PageSlotSorter {
        /**
         * Keeps the sort defined in pageSlots
         */
        DEFAULT() {
            @Override
            public void sort(int[] pageSlots) {
                // Do nothing
            }
        },
        /**
         * Slots are sorted numerically. Slot 15 is before 16 before 17 etc.
         */
        NUMERICAL() {
            @Override
            public void sort(int[] pageSlots) {
                Arrays.sort(pageSlots);
            }
        }
    }
}
