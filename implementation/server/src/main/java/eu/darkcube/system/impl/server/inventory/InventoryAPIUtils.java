/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory;

import java.time.Duration;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface InventoryAPIUtils {
    @NotNull
    Logger LOGGER = LoggerFactory.getLogger("InventoryAPI");

    /**
     * @return the duration of a single server tick
     */
    @NotNull
    Duration tickTime();

    @NotNull
    default Duration defaultTickTime() {
        return InventoryAPIUtilsImpl.DEFAULT_TICK;
    }

    static InventoryAPIUtils utils() {
        return InventoryAPIUtilsImpl.UTILS;
    }
}
