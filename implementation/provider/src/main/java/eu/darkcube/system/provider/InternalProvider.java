/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider;

import java.util.function.Supplier;

public interface InternalProvider {
    static InternalProvider instance() {
        return InternalProviderHolder.PROVIDER;
    }

    <T> T instance(Class<T> cls);

    <T> void register(Class<T> cls, T instance);

    <T> void register(Class<T> cls, Supplier<T> supplier);
}
