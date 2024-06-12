/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.cloudnet;

import eu.darkcube.system.impl.bukkit.version.BukkitVersionLoader;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "UnstableTypeUsedInSignature"})
public class ModernPaperLoader implements PluginLoader {
    private final PluginLoader loader = BukkitVersionLoader.INSTANCE.loadModernMinecraft().createLoader();

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        loader.classloader(classpathBuilder);
    }
}
