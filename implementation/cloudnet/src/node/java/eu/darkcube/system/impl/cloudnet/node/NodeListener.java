/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;
import eu.darkcube.system.impl.cloudnet.DarkCubeSystemModule;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class NodeListener {
    @EventListener
    public void handle(CloudServicePreProcessStartEvent event) {
        copyTo(event.service());
    }

    private void copyTo(CloudService service) {
        try {
            var pluginJar = pluginJarFileName(service);
            if (pluginJar != null) {
                var path = getFile(service.pluginDirectory().resolve(DarkCubeSystemModule.PLUGIN_NAME));
                copyPlugin(pluginJar, path);
            }
            var path = service.directory().resolve(".wrapper").resolve("modules").resolve(DarkCubeSystemModule.PLUGIN_NAME);
            Files.createDirectories(path.getParent());
            var jarOut = new JarOutputStream(Files.newOutputStream(path));
            var names = new HashSet<String>();

            var wrapperIn = getClass().getClassLoader().getResourceAsStream("darkcubesystem-wrapper.jar");
            if (wrapperIn == null) throw new NoSuchFileException("darkcubesystem-wrapper.jar");
            var zipIn = new JarInputStream(wrapperIn);
            merge(names, zipIn, jarOut);

            var injectIn = getClass().getClassLoader().getResourceAsStream("inject/" + pluginJar);
            if (injectIn != null) {
                merge(names, new ZipInputStream(injectIn), jarOut);
            }

            jarOut.close();
        } catch (Throwable throwable) {
            Logger.getGlobal().log(Level.SEVERE, "error during copy for darkcubesystem", throwable);
        }
    }

    private void copyPlugin(String pluginJar, Path path) {
        try {
            var in = getClass().getClassLoader().getResourceAsStream("plugins/" + pluginJar);
            if (in == null) throw new AssertionError("Plugin not found: " + pluginJar);
            Files.copy(in, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getFile(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        return path;
    }

    private void merge(Set<String> names, ZipInputStream in, ZipOutputStream out) throws IOException {
        while (true) {
            var entry = in.getNextEntry();
            if (entry == null) break;
            if (!names.add(entry.getName())) {
                if (!entry.isDirectory()) {
                    Logger.getGlobal().info("Duplicate entry: " + entry.getName());
                }
                continue;
            }
            out.putNextEntry(new ZipEntry(entry));
            in.transferTo(out);
            out.closeEntry();
            in.closeEntry();
        }
        in.close();
    }

    private @Nullable String pluginJarFileName(CloudService service) {
        var env = service.serviceId().environment();
        if (env.equals(ServiceEnvironmentType.MINECRAFT_SERVER)) {
            return "bukkit.jar";
        } else if (env.equals(ServiceEnvironmentType.VELOCITY)) {
            return "velocity.jar";
        } else if (env.equals(ServiceEnvironmentType.MINESTOM)) {
            return "minestom.jar";
        }
        return null;
    }
}
