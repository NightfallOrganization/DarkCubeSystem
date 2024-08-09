/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.standalone;

import eu.darkcube.system.impl.bukkit.version.BukkitVersionLoader;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "UnstableTypeUsedInSignature"})
public class ModernPaperBootstrap implements PluginBootstrap {
    private final PluginBootstrap bootstrap = BukkitVersionLoader.INSTANCE.loadModernMinecraft().createBootstrap();

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        bootstrap.bootstrap(context);
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new BukkitStandalone();
    }
}
