/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.material;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface MaterialProvider {
    @NotNull
    Material of(@NotNull Object platformMaterial) throws IllegalArgumentException;

    @NotNull
    Material spawner() throws UnsupportedOperationException;

    @NotNull
    Material air();
}
