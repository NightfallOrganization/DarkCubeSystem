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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.ServiceLoader;

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
            var url = loadVersionFile();
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

    @SuppressWarnings("ConstantValue")
    private URL loadVersionFile() throws IOException {
        var loader = getClass().getClassLoader();
        if (Bukkit.getServer() == null) {
            // modern minecraft with paper loader
            var version = ServerBuildInfo.buildInfo().minecraftVersionId().replace('.', '_');
            var url = loader.getResource("versions/v" + version + ".jar");
            if (url != null) return url;
        } else {
            // legacy minecraft
            var url = loader.getResource("versions/v1_8_R3.jar");
            if (url != null) return url;
        }
        var versions = new String(Objects.requireNonNull(loader.getResourceAsStream("versions/versions")).readAllBytes(), StandardCharsets.UTF_8).split("\n");
        return loader.getResource("versions/v" + versions[versions.length - 1] + ".jar");
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
