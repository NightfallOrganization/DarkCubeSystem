/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ServiceLoader;

import org.bukkit.Bukkit;

/**
 * Responsible for loading version specific code.
 */
public class BukkitVersionLoader {
    private URLClassLoader classLoader;

    public BukkitVersionHandler load() {
        try {
            if (classLoader != null) throw new IllegalStateException();
            var nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            var path = "versions/" + nmsVersion + ".jar";
            var url = getClass().getClassLoader().getResource(path);
            if (url == null) throw new IllegalStateException("Corrupt DarkCubeSystem Jar: " + path + " not found!");
            var tempFile = Files.createTempFile("darkcubesystem", "version");
            var in = url.openStream();
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            in.close();
            tempFile.toFile().deleteOnExit();
            classLoader = new URLClassLoader(new URL[]{tempFile.toUri().toURL()}, getClass().getClassLoader());
            return ServiceLoader.load(BukkitVersionHandler.class, classLoader).findFirst().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
