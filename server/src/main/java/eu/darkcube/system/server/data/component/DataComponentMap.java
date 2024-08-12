/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.data.component;

import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public sealed interface DataComponentMap extends DataComponent.Holder, Iterable<DataComponent<?>> permits DataComponentMapImpl {
    <T> void set(@NotNull DataComponent<T> component, @NotNull T value);

    <T> void remove(@NotNull DataComponent<T> component);

    @NotNull
    static DataComponentMap create() {
        return new DataComponentMapImpl(Map.of());
    }
}
