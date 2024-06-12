/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.commandapi;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.BukkitVector3d;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.argument.EntityAnchorArgument;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.version.latest.AdventureUtils;
import eu.darkcube.system.libs.com.mojang.brigadier.ResultConsumer;
import eu.darkcube.system.libs.com.mojang.brigadier.context.StringRange;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;

@SuppressWarnings("UnstableApiUsage")
public class CommandConverter {
    private static com.mojang.brigadier.context.StringRange convertRange(StringRange range, int offset) {
        return new com.mojang.brigadier.context.StringRange(range.getStart() + offset, range.getEnd() + offset);
    }

    public static com.mojang.brigadier.suggestion.Suggestions convert(eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions suggestions, int offset) {
        return new com.mojang.brigadier.suggestion.Suggestions(convertRange(suggestions.getRange(), offset), suggestions.getList().stream().map(s -> new Suggestion(convertRange(s.getRange(), offset), s.getText())).toList());
    }

    public static LiteralCommandNode<CommandSourceStack> convertNode(Command command, String name) {
        var cmd = new Cmd(command, name);
        var builder = Commands.literal(name);
        builder.executes(cmd).requires(cmd);
        var argument = Commands.argument("args", StringArgumentType.greedyString());
        argument.suggests(cmd.argumentProvider);
        argument.requires(cmd);
        argument.executes(cmd.argumentCommand);
        builder.then(argument);
        return builder.build();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class Cmd implements com.mojang.brigadier.Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack>, Predicate<CommandSourceStack> {

        private final eu.darkcube.system.bukkit.commandapi.Commands commands;
        private final Command command;
        private final com.mojang.brigadier.Command<CommandSourceStack> argumentCommand;
        private final SuggestionProvider<CommandSourceStack> argumentProvider;

        public Cmd(Command command, String name) {
            this.command = command;
            this.commands = CommandAPI.instance().getCommands();
            this.argumentCommand = context -> run(context, context.getRange().get(context.getInput()));
            this.argumentProvider = (context, builder) -> {
                var offset = context.getLastChild().getRange().getStart();
                return getSuggestions(context, builder, name(context) + " " + builder.getRemaining(), offset);
            };
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
            return getSuggestions(context, builder, context.getRange().get(context.getInput()), context.getRange().getStart());
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            return run(context, context.getRange().get(context.getInput()));
        }

        private String name(CommandContext<?> ctx) {
            return ctx.getLastChild().getNodes().getFirst().getNode().getName();
        }

        private int run(CommandContext<CommandSourceStack> context, String commandLine) {
            try {
                var source = context.getSource();
                var ourSource = createSource(source);
                return commands.executeCommand(ourSource, commandLine);
            } catch (Throwable t) {
                context.getSource().getSender().sendMessage(Component.text("An error occurred during the command execution. Please contact the server administration!", NamedTextColor.RED));
                DarkCubeSystemBukkit.systemPlugin().getSLF4JLogger().error("Failed to run command", t);
                return 0;
            }
        }

        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder, String commandLine, int offset) {
            // var offset = builder.getInput().lastIndexOf(' ');
            var source = createSource(context.getSource());
            var parse = commands.getDispatcher().parse(commandLine, source);
            return commands.getTabCompletions(parse).thenApply(suggestions -> convert(suggestions, offset));
        }

        @Override
        public boolean test(CommandSourceStack source) {
            return source.getSender().hasPermission(command.permission());
        }

        private CommandSource createSource(CommandSourceStack source) {
            var sender = source.getSender();
            var originalExecutor = BukkitCommandExecutor.create(source.getSender());
            var executor = source.getExecutor() == null ? originalExecutor : BukkitCommandExecutor.create(source.getExecutor());
            var pos = BukkitVector3d.position(source.getLocation());
            var world = source.getLocation().getWorld();
            var name = sender.getName();
            @Nullable var entity = source.getExecutor();
            if (entity == null) {
                if (sender instanceof Entity e) {
                    entity = e;
                }
            }
            var rotation = new Vector2f(source.getLocation().getYaw(), source.getLocation().getPitch());
            var feedbackDisabled = false;
            ResultConsumer<CommandSource> resultConsumer = (context1, success, result) -> {
            };
            var extra = new HashMap<String, Object>();
            var displayName = AdventureUtils.convert((entity == null ? sender : entity).name());
            var entityAnchorType = EntityAnchorArgument.Type.FEET;

            return new CommandSource(executor, originalExecutor, pos, world, name, displayName, feedbackDisabled, entity, resultConsumer, entityAnchorType, rotation, extra);
        }
    }
}
