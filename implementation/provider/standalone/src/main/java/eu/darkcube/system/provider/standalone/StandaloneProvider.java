/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider.standalone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import eu.darkcube.system.provider.InternalProvider;

public class StandaloneProvider implements InternalProvider {
    private final Map<Class<?>, Supplier<?>> providers = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> T instance(Class<T> cls) {
        var cached = (T) cache.get(cls);
        if (cached != null) return cached;
        cached = (T) providers.get(cls).get();
        var old = (T) cache.putIfAbsent(cls, cached);
        if (old != null) return old;
        return cached;
    }

    @Override
    public <T> void register(Class<T> cls, T instance) {
        cache.put(cls, instance);
    }

    @Override
    public <T> void register(Class<T> cls, Supplier<T> supplier) {
        providers.put(cls, supplier);
    }
}
