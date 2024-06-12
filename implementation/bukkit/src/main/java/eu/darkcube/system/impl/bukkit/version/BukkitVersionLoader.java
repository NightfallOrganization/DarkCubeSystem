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

import eu.darkcube.system.bukkit.version.BukkitVersion;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.Bukkit;

/**
 * Responsible for loading version specific code.
 */
public class BukkitVersionLoader {
    public static final BukkitVersionLoader INSTANCE = new BukkitVersionLoader();
    private URLClassLoader classLoader;
    private ModernMinecraft modernMinecraft;
    private BukkitVersionHandler versionHandler;

    private BukkitVersionLoader() {
        init();
    }

    private void init() {
        try {
            if (classLoader != null) throw new IllegalStateException();
            String path;
            // noinspection ConstantValue
            if (Bukkit.getServer() == null) {
                // modern minecraft
                path = "versions/v1_20_6.jar";
            } else {
                path = "versions/v1_8_R3.jar";
            }
            var url = getClass().getClassLoader().getResource(path);
            if (url == null) throw new IllegalStateException("Corrupt DarkCubeSystem Jar: " + path + " not found!");
            var tempFile = Files.createTempFile("darkcubesystem", "version");
            var in = url.openStream();
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            in.close();
            tempFile.toFile().deleteOnExit();
            classLoader = new URLClassLoader(new URL[]{tempFile.toUri().toURL()}, getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ModernMinecraft loadModernMinecraft() {
        if (modernMinecraft != null) return modernMinecraft;
        return modernMinecraft = ServiceLoader.load(ModernMinecraft.class, classLoader).findFirst().orElseThrow();
    }

    public BukkitVersionHandler load() {
        if (versionHandler != null) return versionHandler;
        return versionHandler = ServiceLoader.load(BukkitVersionHandler.class, classLoader).findFirst().orElseThrow();
    }
}
