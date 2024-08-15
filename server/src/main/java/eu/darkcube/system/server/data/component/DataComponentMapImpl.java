/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.data.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

record DataComponentMapImpl(@NotNull Map<DataComponent<?>, Object> components) implements DataComponentMap {
    DataComponentMapImpl {
        components = new HashMap<>(components);
    }

    @Override
    public @NotNull Map<DataComponent<?>, Object> components() {
        return Map.copyOf(components);
    }

    @Override
    public boolean has(@NotNull DataComponent<?> component) {
        return components.containsKey(component);
    }

    @Override
    public <T> @Nullable T get(@NotNull DataComponent<T> component) {
        return (T) components.get(component);
    }

    @Override
    public <T> void set(@NotNull DataComponent<T> component, @NotNull T value) {
        components.put(component, value);
    }

    @Override
    public <T> void remove(@NotNull DataComponent<T> component) {
        components.remove(component);
    }

    @Override
    @NotNull
    public Iterator<DataComponent<?>> iterator() {
        return components.keySet().iterator();
    }
}
