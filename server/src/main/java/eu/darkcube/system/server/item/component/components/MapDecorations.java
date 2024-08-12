/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.component.components;

import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record MapDecorations(@NotNull Map<String, Entry> decorations) {
    public MapDecorations {
        decorations = Map.copyOf(decorations);
    }

    public record Entry(@NotNull String type, double x, double z, float rotation) {
    }
}
