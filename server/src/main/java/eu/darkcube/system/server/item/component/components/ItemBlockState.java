/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record ItemBlockState(@NotNull Map<String, String> properties) {
    public ItemBlockState {
        properties = Map.copyOf(properties);
    }
}
