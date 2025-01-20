/*
 * Copyright (c) 2024-2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command;

import eu.darkcube.system.test.TestPlugin;
import eu.darkcube.system.test.command.test.AppendOutCommand;
import eu.darkcube.system.test.command.test.DataStorage;
import eu.darkcube.system.test.command.test.DeployDirCommand;
import eu.darkcube.system.test.command.test.InventoryCommand;
import eu.darkcube.system.test.command.test.NewOutCommand;
import eu.darkcube.system.test.command.test.OpenZipTemplateCommand;
import eu.darkcube.system.test.command.test.ZipTemplateCommand;

public class TestCommand extends BaseCommand {
    public TestCommand(TestPlugin plugin) {
        // @formatter:off
        super("test", b -> b
                .then(new DataStorage(plugin).builder())
                .then(new InventoryCommand().builder())
                
                .then(new ZipTemplateCommand().builder())
                .then(new AppendOutCommand().builder())
                .then(new DeployDirCommand().builder())
                .then(new NewOutCommand().builder())
                .then(new OpenZipTemplateCommand().builder())
                .then(new ZipTemplateCommand().builder())
        );
        // @formatter:on
    }
}
