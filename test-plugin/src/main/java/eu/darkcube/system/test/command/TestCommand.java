package eu.darkcube.system.test.command;

import eu.darkcube.system.test.command.test.InventoryCommand;

public class TestCommand extends BaseCommand {
    public TestCommand() {
        super("test", b -> b.then(new InventoryCommand().builder()));
    }
}
