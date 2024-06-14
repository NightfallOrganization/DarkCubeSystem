/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import eu.darkcube.system.bukkit.version.BukkitVersion;
import eu.darkcube.system.libs.com.mojang.brigadier.CommandDispatcher;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.RequiredArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.command.CommandSender;

@ApiStatus.Internal
public class Commands {

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final Collection<CommandEntry> commandEntries = new HashSet<>();

    public static LiteralArgumentBuilder<CommandSource> literal(String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static Predicate<String> predicate(IParser parser) {
        return bool -> {
            try {
                parser.parse(new StringReader(bool));
                return true;
            } catch (CommandSyntaxException ex) {
                return false;
            }
        };
    }

    public @NotNull CompletableFuture<@NotNull Suggestions> getTabCompletions(ParseResults<CommandSource> parse) {
        return dispatcher.getCompletionSuggestions(parse);
    }

    public @NotNull Suggestions getTabCompletionsSync(ParseResults<CommandSource> parse) {
        try {
            return getTabCompletions(parse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Suggestions.empty().getNow(null);
    }

    public void unregisterByPrefix(String prefix) {
        for (var entry : new HashSet<>(commandEntries)) {
            if (!entry.executor.prefix().equals(prefix)) {
                continue;
            }
            for (var original : new HashSet<>(entry.nodes)) {
                if (unregister(dispatcher.getRoot(), original)) {
                    BukkitVersion.version().commandApiUtils().unregister(original.source.getName());
                    entry.nodes.remove(original);
                }
            }
            if (entry.nodes.isEmpty()) {
                commandEntries.remove(entry);
            }
        }
    }

    public void unregisterPrefixlessByPrefix(String prefix) {
        for (var entry : new HashSet<>(commandEntries)) {
            if (!entry.executor.prefix().equals(prefix)) {
                continue;
            }
            for (var original : new HashSet<>(entry.nodes)) {
                if (!original.isPrefixed) {
                    if (unregister(dispatcher.getRoot(), original)) {
                        BukkitVersion.version().commandApiUtils().unregister(original.source.getName());
                        entry.nodes.remove(original);
                    }
                }
            }
            if (entry.nodes.isEmpty()) {
                commandEntries.remove(entry);
            }
        }
    }

    public CommandEntry unregister(Command command) {
        for (var entry : new ArrayList<>(commandEntries)) {
            if (entry.executor().equals(command)) {
                for (var original : entry.nodes) {
                    unregister(dispatcher.getRoot(), original);
                }
                commandEntries.remove(entry);
                return entry;
            }
        }
        throw new IllegalStateException("Command " + command.name() + " was not found");
    }

    private boolean unregister(CommandNode<CommandSource> parent, CommandEntry.OriginalCommandTree original) {
        var node = parent.getChild(original.source.getName());
        if (node == null) return false;
        for (var o : original.children) {
            unregister(node, o);
        }
        var ncommand = node.getCommand();
        if (ncommand != null && ncommand.equals(original.command)) {
            ncommand = null;
        }
        if (ncommand == null && node.getChildren().isEmpty()) {
            unregisterCommandNodeChild(parent, CommandNode.class, node.getName());
            return true;
        }
        return false;
    }

    public CommandEntry register(Command executor) {
        Collection<CommandEntry.OriginalCommandTree> nodes = new HashSet<>();
        for (var name : executor.names()) {
            nodes.add(new CommandEntry.OriginalCommandTree(dispatcher.register(executor.builder(name)), false));
            nodes.add(new CommandEntry.OriginalCommandTree(dispatcher.register(executor.builder(executor.prefix() + ":" + name)), true));
        }
        var entry = new CommandEntry(executor, nodes);
        commandEntries.add(entry);
        return entry;
    }

    public Collection<CommandEntry> commandEntries() {
        return commandEntries;
    }

    public void executeCommand(CommandSender sender, String commandLine) {
        executeCommand(CommandSource.create(sender), commandLine);
    }

    public int executeCommand(CommandSource source, final String commandLine) {
        var parse = dispatcher.parse(commandLine, source);
        try {
            return dispatcher.execute(parse);
        } catch (CommandSyntaxException ex) {
            var failedCursor = ex.getCursor();
            if (failedCursor == 0) {
                return -1; // Happens when someone tries to execute a main command (PluginCommand) that requires a condition which is not met
            }

            source.sendMessage(Component.text(ex.getMessage(), NamedTextColor.RED));

            if (failedCursor == -1) {
                return -1; // When there is no context to the exception. Use #createWithContext to work around this
            }

            if (failedCursor == commandLine.length()) {
                final var commandLineNext = commandLine + " ";
                var parse2 = dispatcher.parse(commandLineNext, source);
                getTabCompletions(parse2).thenAccept(completions -> source.sendCompletions(commandLineNext, completions, usages(parse2)));
            } else {
                getTabCompletions(parse).thenAccept(completions2 -> source.sendCompletions(commandLine, completions2, usages(parse)));
            }
            return -1;
        } catch (Throwable ex) {
            var writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            var msgs = writer.getBuffer().toString().replace("\t", "  ").split("(\r\n|\r|\n)");
            Component c = Component.text("");
            for (var i = 0; i < msgs.length; i++) {
                if (i != 0) c = c.appendNewline();
                c = c.append(Component.text(msgs[i]).color(NamedTextColor.DARK_RED));
            }
            AdventureSupport.adventureSupport().audienceProvider().console().sendMessage(c);
            source.sendMessage(Component.text("An error occurred while attempting to execute the command", NamedTextColor.RED));
            return -1;
        }
    }

    private Map<CommandNode<CommandSource>, String> usages(ParseResults<CommandSource> parse) {
        var suggestionContext = parse.getContext().findSuggestionContext(parse.getReader().getTotalLength());
        return dispatcher.getSmartUsage(suggestionContext.parent, parse.getContext().getSource());
    }

    public CommandDispatcher<CommandSource> getDispatcher() {
        return dispatcher;
    }

    public static void unregisterCommandNodeChild(Object rootNode, Class<?> commandNodeClass, String name) {
        try {
            var childrenField = commandNodeClass.getDeclaredField("children");
            var literalsField = commandNodeClass.getDeclaredField("literals");
            var argumentsField = commandNodeClass.getDeclaredField("arguments");
            unregisterCommandNodeChild(childrenField, rootNode, name);
            unregisterCommandNodeChild(literalsField, rootNode, name);
            unregisterCommandNodeChild(argumentsField, rootNode, name);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    private static void unregisterCommandNodeChild(Field field, Object instance, String name) throws IllegalAccessException {
        field.setAccessible(true);
        var map = (Map<String, ?>) field.get(instance);
        map.remove(name);
    }

    @FunctionalInterface
    public interface IParser {
        void parse(StringReader reader) throws CommandSyntaxException;
    }

    public record CommandEntry(Command executor, Collection<OriginalCommandTree> nodes) {
        public static class OriginalCommandTree {
            public final eu.darkcube.system.libs.com.mojang.brigadier.Command<CommandSource> command;
            public final CommandNode<CommandSource> source;
            public final Collection<OriginalCommandTree> children;
            public final boolean isPrefixed;

            public OriginalCommandTree(CommandNode<CommandSource> node, boolean isPrefixed) {
                this.source = node;
                this.command = node.getCommand();
                this.children = new HashSet<>();
                this.isPrefixed = isPrefixed;
                for (var child : node.getChildren()) {
                    children.add(new OriginalCommandTree(child, false));
                }
            }

            @Override
            public String toString() {
                return "OriginalCommandTree{" + "command=" + command + ", source=" + source + ", children=" + children + ", isPrefixed=" + isPrefixed + '}';
            }
        }
    }
}
