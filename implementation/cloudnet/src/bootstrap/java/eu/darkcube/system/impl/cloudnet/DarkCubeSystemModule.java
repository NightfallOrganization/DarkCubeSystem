/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;

import dev.derklaro.aerogel.Injector;
import dev.derklaro.aerogel.Name;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.darkcube.system.impl.agent.AgentAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkCubeSystemModule extends DriverModule {
    static final Logger LOGGER = LoggerFactory.getLogger("DarkCubeSystem");
    public static final String PLUGIN_NAME;

    @ModuleTask(lifecycle = ModuleLifeCycle.LOADED)
    public void loaded(@Name("dataDirectory") Path dataDirectory) {
        try {
            injectJar(dataDirectory, "lib.jar");
            injectJar(dataDirectory, "inject.jar");
        } catch (Throwable e) {
            LOGGER.error("Failed to inject jar", e);
        }
    }

    private void injectJar(Path dataDirectory, String name) throws IOException {
        var stream = getClass().getClassLoader().getResourceAsStream(name);
        if (stream != null) {
            LOGGER.debug("Injecting {}", name);
            var path = dataDirectory.resolve(name);
            Files.createDirectories(dataDirectory);
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            var file = path.toFile();
            AgentAccess.instrumentation().appendToSystemClassLoaderSearch(new JarFile(file));
        } else {
            LOGGER.debug("Unable to find {}", name);
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    void start(Injector injector) {
        try {
            injector.instance(ModuleLoader.class);
            ModuleStopper.class.getName(); // Init ModuleStopper
        } catch (Throwable t) {
            LOGGER.error("Failed to start module", t);
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    void stop(Injector injector) {
        try {
            injector.instance(ModuleStopper.class);
        } catch (Throwable t) {
            LOGGER.error("Failed to stop module", t);
        }
    }

    static {
        try {
            // Thread.sleep(10000);
            PLUGIN_NAME = Path.of(DarkCubeSystemModule.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getFileName().toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
