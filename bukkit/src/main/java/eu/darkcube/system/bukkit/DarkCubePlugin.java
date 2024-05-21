/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.event.Listener;

public class DarkCubePlugin extends Plugin implements Listener {

    private static DarkCubePlugin systemPlugin = null;

    public DarkCubePlugin(Key key) {
        super(key);
    }

    public DarkCubePlugin(String pluginName) {
        super(pluginName);
    }

    protected static void systemPlugin(DarkCubePlugin systemPlugin) {
        DarkCubePlugin.systemPlugin = systemPlugin;
    }

    public static DarkCubePlugin systemPlugin() {
        return systemPlugin;
    }

    @Override
    public String getCommandPrefix() {
        return "§5Dark§dCube";
    }
}
