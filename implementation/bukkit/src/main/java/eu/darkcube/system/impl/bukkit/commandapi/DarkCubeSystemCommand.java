/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.commandapi;

import static eu.darkcube.system.bukkit.commandapi.Commands.argument;
import static eu.darkcube.system.bukkit.commandapi.Commands.literal;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class DarkCubeSystemCommand extends Command {
    public DarkCubeSystemCommand() {
        super("darkcubesystem", "darkcubesystem", new String[]{"dcs", "dc"}, b -> b.then(literal("commands").then(literal("unregister").then(argument("command", new CommandArgument()).executes(ctx -> {
            var command = ctx.getArgument("command", Command.class);
            CommandAPI.instance().unregister(command);
            ctx.getSource().sendMessage(Component.text("Unregistered " + command.name()));
            return 0;
        })))));
    }

    private static class CommandArgument implements ArgumentType<Command> {
        private static final DynamicCommandExceptionType NOT_FOUND = Messages.INVALID_ENUM.newDynamicCommandExceptionType();

        @Override
        public Command parse(StringReader reader) throws CommandSyntaxException {
            var name = reader.readUnquotedString();
            var entryOptional = CommandAPI.instance().getCommands().commandEntries().stream().filter(entry -> entry.executor().name().equals(name)).findFirst();
            for (var child : CommandAPI.instance().getCommands().getDispatcher().getRoot().getChildren()) {
                System.out.println(child.getName());
            }
            if (entryOptional.isEmpty()) throw NOT_FOUND.createWithContext(reader, name);
            var entry = entryOptional.get();
            return entry.executor();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(CommandAPI.instance().getCommands().commandEntries().stream().map(entry -> entry.executor().name()), builder);
        }
    }
}
