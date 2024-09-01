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
        CommandAPI.instance().register(new TestCommand());
    }
}
