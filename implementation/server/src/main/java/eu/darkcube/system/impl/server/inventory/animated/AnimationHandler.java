/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.animated;

import eu.darkcube.system.impl.server.inventory.TemplateInventoryImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;

public interface AnimationHandler<PlatformItem> {

    void setItem(@NotNull TemplateInventoryImpl<PlatformItem> inventory, int slot, @NotNull PlatformItem item);

    static <T> AnimationHandler<T> noAnimation() {
        return Inventory::setItem;
    }
}
