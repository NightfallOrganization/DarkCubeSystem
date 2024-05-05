/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.item.material;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface Material {

    /**
     * Converts the material type of the platform to this material type.
     *
     * @param platformMaterial the platform material type
     * @return the ItemBuilder material type
     */
    static @NotNull Material of(@NotNull Object platformMaterial) {
        return MaterialProviderImpl.of(platformMaterial);
    }

    /**
     * Same as {@link #of(Object)}, returns {@link #air()} if null.
     *
     * @param platformMaterial the platform material type
     * @return the ItemBuilder material type
     */
    static @NotNull Material ofNullable(@Nullable Object platformMaterial) {
        return platformMaterial == null ? air() : of(platformMaterial);
    }

    static @NotNull Material air() {
        return MaterialProviderImpl.air();
    }

    static @NotNull Material spawner() {
        return MaterialProviderImpl.spawner();
    }
}
