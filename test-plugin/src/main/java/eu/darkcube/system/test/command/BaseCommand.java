package eu.darkcube.system.test.command;

import java.util.function.Consumer;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class BaseCommand extends Command {
    public BaseCommand(String name, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        this(name, new String[0], argumentBuilder);
    }

    public BaseCommand(String name, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("testplugin", name, aliases, argumentBuilder);
    }

    public BaseCommand(String name, String[] aliases, String description, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("testplugin", name, aliases, description, argumentBuilder);
    }

    public BaseCommand(String name, String permission, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("testplugin", name, permission, aliases, argumentBuilder);
    }

    public BaseCommand(String name, String permission, String description, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("testplugin", name, permission, description, aliases, argumentBuilder);
    }
}
