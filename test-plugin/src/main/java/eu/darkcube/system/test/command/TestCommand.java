/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command;

import eu.darkcube.system.test.TestPlugin;
import eu.darkcube.system.test.command.test.DataStorage;
import eu.darkcube.system.test.command.test.InventoryCommand;

public class TestCommand extends BaseCommand {
    public TestCommand(TestPlugin plugin) {
        super("test", b -> b.then(new InventoryCommand().builder()).then(new DataStorage(plugin).builder()));
    }
}
