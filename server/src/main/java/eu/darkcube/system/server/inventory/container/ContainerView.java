/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;

public interface ContainerView {
    @NotNull
    TemplateInventory inventory();

    /**
     * @return the container these settings are for
     */
    @NotNull
    Container container();

    /**
     * @return the item display priority (layer)
     */
    int priority();

    /**
     * @return the priority at which this container will be selected when shift-clicking an item into this container.
     */
    int slotPriority();

    void slotPriority(int priority);

    void slots(int @NotNull ... slots);

    int @NotNull [] slots();
}
