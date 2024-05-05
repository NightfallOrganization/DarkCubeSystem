/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface Provider {

    static Provider newProvider() {
        return new ProviderImpl();
    }

    <T> void register(@NotNull Class<T> serviceClass, @NotNull T service);

    <T> @NotNull T service(@NotNull Class<T> serviceClass);

    class ServiceNotRegisteredException extends RuntimeException {
        public ServiceNotRegisteredException(String message) {
            super(message);
        }
    }
}
