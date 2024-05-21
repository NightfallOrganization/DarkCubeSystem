/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Locale;

import dev.derklaro.aerogel.SpecifiedInjector;
import dev.derklaro.reflexion.Reflexion;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.darkcube.system.util.AsyncExecutor;
import jakarta.inject.Named;

public class DarkCubeSystemModule extends DriverModule {
    public static final String PLUGIN_NAME;
    private ModuleImplementation implementation = null;

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void start(@Named("module") InjectionLayer<SpecifiedInjector> injectionLayer, ComponentInfo componentInfo) {
        AsyncExecutor.start();
        try {
            var environmentName = componentInfo.environment().name();
            var simpleName = "DarkCubeSystem" + environmentName.substring(0, 1).toUpperCase(Locale.ROOT) + environmentName.substring(1);
            var className = getClass().getPackageName() + "." + environmentName + "." + simpleName;
            var cls = Class.forName(className);
            var reflexion = Reflexion.on(cls);

            implementation = injectionLayer.instance(reflexion.getWrappedClass().asSubclass(ModuleImplementation.class));
            implementation.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void stop() {
        if (implementation == null) return;
        implementation.stop();
        implementation = null;
        AsyncExecutor.stop();
    }

    static {
        try {
            PLUGIN_NAME = Path.of(DarkCubeSystemModule.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getFileName().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
