/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.argument;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class BooleanArgument implements ArgumentType<Boolean> {
    private static final DynamicCommandExceptionType BOOLEAN_INVALID = Messages.BOOLEAN_INVALID.newDynamicCommandExceptionType();

    private BooleanArgument() {
    }

    public static BooleanArgument booleanArgument() {
        return new BooleanArgument();
    }

    public static boolean getBoolean(CommandContext<?> context, String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override public Boolean parse(StringReader reader) throws CommandSyntaxException {
        var s = reader.readUnquotedString();
        var type = BooleanType.getByName(s);
        if (type == null) {
            throw BOOLEAN_INVALID.createWithContext(reader, s);
        }
        return type.getValue();
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(BooleanType.BY_NAME.keySet(), builder);
    }

    private enum BooleanType {

        TRUE("true", true),

        FALSE("false", false);

        private static final Map<String, BooleanType> BY_NAME = new HashMap<>();

        static {
            for (var type : values()) {
                BY_NAME.put(type.key.toLowerCase(Locale.ROOT), type);
            }
        }

        private final String key;
        private final boolean value;

        BooleanType(String key, boolean value) {
            this.key = key;
            this.value = value;
        }

        public static BooleanType getByName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public boolean getValue() {
            return value;
        }
    }
}
