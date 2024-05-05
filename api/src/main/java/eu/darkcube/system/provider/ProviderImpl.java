/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class ProviderImpl implements Provider {
    private final Map<Class<?>, Object> services = new HashMap<>();

    @Override
    public <T> void register(@NotNull Class<T> serviceClass, @NotNull T service) {
        services.put(serviceClass, service);
    }

    @Override
    public <T> @NotNull T service(@NotNull Class<T> serviceClass) {
        Object o = services.get(serviceClass);
        if (o == null) throw new ServiceNotRegisteredException(serviceClass.getName());
        return serviceClass.cast(o);
    }
}
