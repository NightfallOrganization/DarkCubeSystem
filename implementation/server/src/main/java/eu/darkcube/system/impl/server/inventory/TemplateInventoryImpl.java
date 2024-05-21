/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.time.Duration;
import java.time.Instant;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.TemplateInventory;

public interface TemplateInventoryImpl<PlatformItem> extends TemplateInventory {
    @NotNull
    Instant openInstant();

    void scheduleSetItem(int slot, @NotNull Duration duration, @NotNull PlatformItem item);
}
