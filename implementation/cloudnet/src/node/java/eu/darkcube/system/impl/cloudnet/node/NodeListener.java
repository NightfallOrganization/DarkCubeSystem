/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import eu.cloudnetservice.common.io.FileUtil;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;
import eu.darkcube.system.impl.cloudnet.DarkCubeSystemModule;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class NodeListener {
    private final Path cacheDirectory = FileUtil.TEMP_DIR.resolve("caches").resolve("darkcubesystem");
    private final AtomicLong lastUpdate = new AtomicLong(System.nanoTime());
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final AtomicBoolean newContent = new AtomicBoolean(false);

    public NodeListener() {
        try {
            var path = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            var watchService = path.getFileSystem().newWatchService();
            var key = path.getParent().register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            Thread.startVirtualThread(() -> {
                // noinspection InfiniteLoopStatement
                while (true) {
                    if (!newContent.get()) {
                        lock.lock();
                        condition.awaitUninterruptibly();
                        lock.unlock();
                    }
                    if (!newContent.get()) continue;

                    while (true) {
                        var diffNanos = System.nanoTime() - lastUpdate.get();
                        var diff = TimeUnit.NANOSECONDS.toMillis(diffNanos);
                        if (diff < 1000) {
                            try {
                                // noinspection BusyWait
                                Thread.sleep(diff);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            continue;
                        }
                        try {
                            newContent.set(false);
                            computeCaches(path);
                            if (newContent.get()) continue;
                            Logger.getLogger("DarkCubeSystem").info("Updated caches from file");
                        } catch (Throwable t) {
                            Logger.getLogger("DarkCubeSystem").info("Failed to recompute caches: " + t.getLocalizedMessage());
                        }
                        break;
                    }

                }
            });
            Thread.startVirtualThread(() -> {
                while (true) {
                    try {
                        var k = watchService.take();
                        if (k != key) {
                            Logger.getLogger("DarkCubeSystem").info("Wrong key");
                            continue;
                        }
                        var events = key.pollEvents();
                        if (!events.isEmpty()) {
                            lastUpdate.set(System.nanoTime());
                            newContent.set(true);
                            lock.lock();
                            condition.signal();
                            lock.unlock();
                        }
                        key.reset();
                    } catch (ClosedWatchServiceException e) {
                        e.printStackTrace();
                        break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            computeCaches(path);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void computeCaches(Path path) throws IOException {
        var jarIn = new ZipInputStream(Files.newInputStream(path));
        var caches = new HashMap<String, byte[]>();
        while (true) {
            var entry = jarIn.getNextEntry();
            if (entry == null) break;
            if (entry.isDirectory()) continue;
            var cache = entry.getName().startsWith("inject/") || entry.getName().startsWith("plugins/") || entry.getName().equals("darkcubesystem-wrapper.jar");
            if (cache) {
                caches.put(entry.getName(), jarIn.readAllBytes());
            }
            jarIn.closeEntry();
        }
        jarIn.close();

        // update caches
        FileUtil.delete(cacheDirectory);
        Files.createDirectories(cacheDirectory);
        for (var entry : caches.entrySet()) {
            var name = entry.getKey();
            var bytes = entry.getValue();
            var file = cacheDirectory.resolve(name);
            Files.createDirectories(file.getParent());
            Files.write(file, bytes);
        }
    }

    private InputStream stream(String path) throws IOException {
        var file = cacheDirectory.resolve(path);
        if (Files.exists(file)) {
            return Files.newInputStream(file);
        }
        return null;
    }

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

            var wrapperIn = stream("darkcubesystem-wrapper.jar");
            if (wrapperIn == null) throw new NoSuchFileException("darkcubesystem-wrapper.jar");
            var zipIn = new JarInputStream(wrapperIn);
            merge(names, zipIn, jarOut);

            var injectIn = stream("inject/" + pluginJar);
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
            var in = stream("plugins/" + pluginJar);
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
