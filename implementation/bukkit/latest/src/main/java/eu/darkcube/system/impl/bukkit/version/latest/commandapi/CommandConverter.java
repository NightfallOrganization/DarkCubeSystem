/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.commandapi;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.libs.com.mojang.brigadier.context.StringRange;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.v1_20_R2.command.VanillaCommandWrapper;

public class CommandConverter {
    private final Command command;
    private final Commands dispatcher;

    public CommandConverter(Command command) {
        this.command = command;
        this.dispatcher = MinecraftServer.getServer().vanillaCommandDispatcher;
    }

    private static com.mojang.brigadier.context.StringRange convertRange(StringRange range, int offset) {
        return new com.mojang.brigadier.context.StringRange(range.getStart() + offset, range.getEnd() + offset);
    }

    public static com.mojang.brigadier.suggestion.Suggestions convert(eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions suggestions, int offset) {
        return new com.mojang.brigadier.suggestion.Suggestions(convertRange(suggestions.getRange(), offset), suggestions
                .getList()
                .stream()
                .map(s -> new Suggestion(convertRange(s.getRange(), offset), s.getText()))
                .toList());
    }

    public VanillaCommandWrapper[] convert() {
        var cmd = new Cmd(command);
        var wrappers = new VanillaCommandWrapper[command.getNames().length * 2];
        var i = 0;
        for (var name : command.getNames()) {
            name = name.toLowerCase(Locale.ROOT);
            wrappers[i++] = create(name, cmd);
            wrappers[i++] = create(command.getPrefix().toLowerCase(Locale.ROOT) + ":" + name, cmd);
        }
        return wrappers;
    }

    private VanillaCommandWrapper create(String name, Cmd cmd) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> b = com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(name.toLowerCase(Locale.ROOT));
        b.executes(cmd);
        b.requires(cmd);
        RequiredArgumentBuilder<CommandSourceStack, String> a = RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString());
        a.suggests(cmd);
        a.executes(cmd);
        b.then(a);

        var n = b.build();
        return new VanillaCommandWrapper(dispatcher, n);
    }

    private static class Cmd implements com.mojang.brigadier.Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack>, Predicate<CommandSourceStack> {

        private final eu.darkcube.system.bukkit.commandapi.Commands commands;
        private final Command command;

        public Cmd(Command command) {
            this.command = command;
            this.commands = CommandAPI.instance().getCommands();
        }

        @Override public int run(CommandContext<CommandSourceStack> context) {
            try {
                commands.executeCommand(context.getSource().getBukkitSender(), context.getRange().get(context.getInput()));
                return com.mojang.brigadier.Command.SINGLE_SUCCESS;
            } catch (Throwable t) {
                context
                        .getSource()
                        .getBukkitSender()
                        .sendMessage(Component.text("An error occurred during the command execution. Please contact the server administration!", NamedTextColor.RED));
                DarkCubeSystemBukkit.systemPlugin().getSLF4JLogger().error("Failed to run command", t);
                return 0;
            }
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
            var line = builder.getInput();
            var offsetCalc = 0;
            if (line.startsWith("/")) {
                line = line.substring(1);
                offsetCalc = 1;
            }
            var offset = offsetCalc;
            var source = CommandSource.create(context.getSource().getBukkitSender());
            var parse = commands.getDispatcher().parse(line, source);
            return commands.getTabCompletions(parse).thenApply(sug -> convert(sug, offset));
        }

        @Override public boolean test(CommandSourceStack source) {
            return source.getBukkitSender().hasPermission(command.getPermission());
        }
    }
}
