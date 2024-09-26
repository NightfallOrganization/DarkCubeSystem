/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.test.command.TestCommand;

public class TestPlugin extends DarkCubePlugin {
    public TestPlugin() {
        super("testplugin");
    }

    @Override
    public void onEnable() {
        CommandAPI.instance().register(new TestCommand(this));
    }
}
