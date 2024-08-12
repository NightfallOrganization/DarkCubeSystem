/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.data.component;

import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface DataComponent<T> extends Keyed {
    interface Holder {
        boolean has(@NotNull DataComponent<?> component);

        <T> @Nullable T get(@NotNull DataComponent<T> component);

        default <T> @NotNull T get(@NotNull DataComponent<T> component, @NotNull T defaultValue) {
            final var value = get(component);
            return value != null ? value : defaultValue;
        }
    }
}
