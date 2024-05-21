/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class EnumArgument<T extends Enum<?>> implements ArgumentType<T> {

    private static final DynamicCommandExceptionType INVALID_ENUM = Messages.INVALID_ENUM.newDynamicCommandExceptionType();

    private final Function<T, String[]> toStringFunction;
    private final Function<String, T> fromStringFunction;

    private final T[] values;

    private EnumArgument(T[] values) {
        this(values, null, null);
    }

    private EnumArgument(T[] values, Function<T, String[]> toStringFunction, Function<String, T> fromStringFunction) {
        this.values = values;
        this.toStringFunction = toStringFunction == null ? defaultToStringFunction() : toStringFunction;
        this.fromStringFunction = fromStringFunction == null ? defaultFromStringFunction() : fromStringFunction;
    }

    public static <T extends Enum<?>> T getEnumArgument(CommandContext<CommandSource> context, String name, Class<T> enumClass) {
        return context.getArgument(name, enumClass);
    }

    public static <T extends Enum<?>> EnumArgument<T> enumArgument(T[] values) {
        return new EnumArgument<>(values);
    }

    public static <T extends Enum<?>> EnumArgument<T> enumArgument(T[] values, Function<T, String[]> toStringFunction) {
        return new EnumArgument<>(values, toStringFunction, null);
    }

    private Function<String, T> defaultFromStringFunction() {
        final Map<String, T> map = new HashMap<>();
        for (var t : values) {
            var arr = toStringFunction.apply(t);
            for (var s : arr) {
                if (map.containsKey(s)) {
                    System.out.println("[EnumArgument] Ambiguous name: " + s);
                } else {
                    map.put(s, t);
                }
            }
        }
        return map::get;
    }

    private Function<T, String[]> defaultToStringFunction() {
        final Map<T, String[]> map = new HashMap<>();
        for (var t : values) {
            map.put(t, new String[]{t.name()});
        }
        return map::get;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();
        var in = reader.readUnquotedString();
        var type = fromStringFunction.apply(in);
        if (type == null) {
            reader.setCursor(cursor);
            throw INVALID_ENUM.createWithContext(reader, in);
        }
        return type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        for (var t : values) {
            suggestions.addAll(Arrays.asList(toStringFunction.apply(t)));
        }
        return ISuggestionProvider.suggest(suggestions, builder);
    }
}
