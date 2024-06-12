/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest;

import eu.darkcube.system.impl.bukkit.version.ModernMinecraft;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.loader.PluginLoader;

public class ModernPaper implements ModernMinecraft {
    @Override
    public PluginLoader createLoader() {
        return new ModernLoader();
    }

    @Override
    public PluginBootstrap createBootstrap() {
        return new ModernBootstrap();
    }
}
