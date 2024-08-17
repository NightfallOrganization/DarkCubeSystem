/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.inventory;

import java.time.Duration;

import eu.darkcube.system.impl.server.inventory.InventoryAPIUtils;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.utils.time.TimeUnit;

public class MinestomInventoryAPIUtils implements InventoryAPIUtils {
    private final Duration tickTime = TimeUnit.SERVER_TICK.getDuration();

    @Override
    public @NotNull Duration tickTime() {
        return tickTime;
    }
}
