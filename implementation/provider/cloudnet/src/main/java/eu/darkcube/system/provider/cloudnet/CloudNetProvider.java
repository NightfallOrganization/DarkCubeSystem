/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider.cloudnet;

import java.util.function.Supplier;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.provider.InternalProvider;

public class CloudNetProvider implements InternalProvider {
    private final InjectionLayer<?> layer = InjectionLayer.fresh("cloudnet_provider");

    @Override
    public <T> T instance(Class<T> cls) {
        return layer.instance(cls);
    }

    @Override
    public <T> void register(Class<T> cls, T instance) {
        layer.install(BindingBuilder.create().bind(cls).toInstance(instance));
    }

    @Override
    public <T> void register(Class<T> cls, Supplier<T> supplier) {
        layer.install(BindingBuilder.create().bind(cls).toProvider(supplier::get));
    }
}
